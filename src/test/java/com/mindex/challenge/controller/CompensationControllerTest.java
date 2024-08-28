package com.mindex.challenge.controller;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.dto.CompensationCreationRequest;
import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.mindex.challenge.testingutils.TestBuilderUtil.buildHttpHeadersWithJsonContentType;
import static com.mindex.challenge.testingutils.TestFunctionUtil.buildUrlWithPortAndPath;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationControllerTest {

    private String compensationUrl;
    private String compensationForEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        compensationUrl = buildUrlWithPortAndPath(port, "/compensation");
        compensationForEmployeeIdUrl = buildUrlWithPortAndPath(port, "/compensation/{employeeId}");
    }

    @Test
    public void ensure400Response_whenFetchCurrentCompensationForEmployeeId_givenInvalidEmployeeIdFormatIsProvided() {
        //Given
        String employeeId = "invalidUUIDFormat";

        //When
        Map<String, Object> actual = restTemplate.getForEntity(compensationForEmployeeIdUrl, Map.class, employeeId).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/compensation/invalidUUIDFormat", actual.get("path"));
    }

    @Test
    public void ensure400Response_whenCreateNewCompensationForEmployee_givenInvalidEmployeeIdFormatIsProvided() {
        //Given
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        CompensationCreationRequest requestBody = new CompensationCreationRequest("invalidUUIDFormat", 100);

        //When
        Map<String, Object> actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers), Map.class).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/compensation", actual.get("path"));
    }

    @Test
    public void ensure404Response_whenFetchCurrentCompensationForEmployeeId_givenEmployeeIdWithNoMatchInDb() {
        //Given
        String employeeId = UUID.randomUUID().toString();

        //When
        ResponseEntity<String> actual = restTemplate.getForEntity(compensationForEmployeeIdUrl, String.class, employeeId);

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        String expectedBody = String.format("Failed to fetch compensation for employeeId: %s", employeeId);
        assertEquals(expectedBody, actual.getBody());
    }

    @Test
    public void ensure400Response_whenCreateNewCompensationForEmployee_givenInvalidSalaryIsProvided() {
        //Given
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        CompensationCreationRequest requestBody = new CompensationCreationRequest(UUID.randomUUID().toString(), -1);

        //When
        Map<String, Object> actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers), Map.class).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/compensation", actual.get("path"));
    }

    @Test
    public void ensure404Response_whenCreateNewCompensationForEmployee_givenEmployeeIdWithNoMatchInDb() {
        //Given
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        String employeeId = UUID.randomUUID().toString();
        CompensationCreationRequest compensationCreationRequest = new CompensationCreationRequest(employeeId, 100);

        //When
        ResponseEntity<String> actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(compensationCreationRequest, headers), String.class);

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        String expectedBody = String.format("Failed to read employee with id: %s", employeeId);
        assertEquals(expectedBody, actual.getBody());
    }

    @Test
    public void ensureCompensationWasCreated_whenCreateNewCompensationForEmployee_givenValidCompensationRequest() {
        //Given
        Instant beforeRequestExecutionTime = Instant.now();
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        Employee employee = createAndInsertNewEmployeeIntoDb();
        CompensationCreationRequest compensationCreationRequest = new CompensationCreationRequest(employee.getEmployeeId(), 100);

        //When
        Compensation actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(compensationCreationRequest, headers), Compensation.class).getBody();

        //Then
        assertNotNull(actual);
        assertNotNull(actual.getCompensationId());
        assertEquals(compensationCreationRequest.getEmployeeId(), actual.getEmployeeId());
        assertEquals(compensationCreationRequest.getSalary(), actual.getSalary());
        assertEquals(employee.getDepartment(), actual.getDepartment());
        assertEquals(employee.getPosition(), actual.getPosition());
        assertNotNull(actual.getEffectiveDate());
        assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeRequestExecutionTime));
        assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
    }

    @Test
    public void ensureMostRecentCompensationIsReturned_whenFetchCurrentCompensationForEmployeeId_givenValidEmployeeId() {
        //Given
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        Employee employee = createAndInsertNewEmployeeIntoDb();
        //Creates up to 4 new compensations for one employee, ensuring that the get request always returns the employee's most recent compensation
        for (int i = 1; i <= 4; i++) {
            Instant beforeRequestExecutionTime = Instant.now();
            employee.setDepartment("Department_" + i);
            employee.setPosition("Position_" + i);
            employee = employeeRepository.save(employee);
            CompensationCreationRequest compensationCreationRequest = new CompensationCreationRequest(employee.getEmployeeId(), 100 * i);

            Compensation expected = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                    new HttpEntity<>(compensationCreationRequest, headers), Compensation.class).getBody();
            assertNotNull(expected);

            //When
            Compensation actual = restTemplate.getForEntity(compensationForEmployeeIdUrl, Compensation.class, employee.getEmployeeId()).getBody();

            //Then
            assertNotNull(actual);
            assertNotNull(actual.getCompensationId());
            assertEquals(employee.getEmployeeId(), actual.getEmployeeId());
            assertEquals(employee.getDepartment(), actual.getDepartment());
            assertEquals(employee.getPosition(), actual.getPosition());
            assertEquals(expected.getSalary(), actual.getSalary());
            assertNotNull(actual.getEffectiveDate());
            assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeRequestExecutionTime));
            assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
        }
    }

    private Employee createAndInsertNewEmployeeIntoDb() {
        Employee employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName("John");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");

        return employeeRepository.insert(employee);
    }
}
