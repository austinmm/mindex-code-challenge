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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
    public void ensureCompensationWasCreated_whenCreateNewCompensationForEmployee_givenValidCompensationRequest() {
        //Given
        Instant beforeRequestExecutionTime = Instant.now();
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        Employee employee = createAndInsertNewEmployeeInDb();
        CompensationCreationRequest requestBody = new CompensationCreationRequest(employee.getEmployeeId(), 100);

        //When
        Compensation actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers), Compensation.class).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(requestBody.getEmployeeId(), actual.getEmployeeId());
        assertEquals(requestBody.getSalary(), actual.getSalary());
        assertEquals(employee.getDepartment(), actual.getDepartment());
        assertEquals(employee.getPosition(), actual.getPosition());
        assertNotNull(actual.getEffectiveDate());
        assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeRequestExecutionTime));
        assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
    }

    @Test
    public void ensureMostRecentCompensationIsReturned_whenFetchCurrentCompensationForEmployeeId_givenValidEmployeeId() {
        //Given
        Instant beforeRequestExecutionTime = Instant.now();
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        Employee employee = createAndInsertNewEmployeeInDb();
        CompensationCreationRequest requestBody = new CompensationCreationRequest(employee.getEmployeeId(), 100);

        //When
        Compensation actual = restTemplate.exchange(compensationUrl, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers), Compensation.class).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(requestBody.getEmployeeId(), actual.getEmployeeId());
        assertEquals(requestBody.getSalary(), actual.getSalary());
        assertEquals(employee.getDepartment(), actual.getDepartment());
        assertEquals(employee.getPosition(), actual.getPosition());
        assertNotNull(actual.getEffectiveDate());
        assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeRequestExecutionTime));
        assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
    }

    private static void assertCompensationResponseIsCorrect(Compensation actual, Employee employee, CompensationCreationRequest compensationCreationRequest, Instant beforeRequestExecutionTime) {
        assertNotNull(actual);
        assertEquals(compensationCreationRequest.getEmployeeId(), actual.getEmployeeId());
        assertEquals(compensationCreationRequest.getSalary(), actual.getSalary());
        assertEquals(employee.getDepartment(), actual.getDepartment());
        assertEquals(employee.getPosition(), actual.getPosition());
        assertNotNull(actual.getEffectiveDate());
        assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeRequestExecutionTime));
        assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
    }

    private Employee createAndInsertNewEmployeeInDb() {
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
