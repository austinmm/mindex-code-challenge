package com.mindex.challenge.controller;

import com.mindex.challenge.model.Employee;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.testingutils.TestBuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.mindex.challenge.testingutils.TestAssertionUtil.assertEmployeeEquivalence;
import static com.mindex.challenge.testingutils.TestBuilderUtil.buildHttpHeadersWithJsonContentType;
import static com.mindex.challenge.testingutils.TestFunctionUtil.buildUrlWithPortAndPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = buildUrlWithPortAndPath(port, "/employee");
        employeeIdUrl = buildUrlWithPortAndPath(port, "/employee/{id}");
    }

    @Test
    public void ensure400Response_whenRead_givenInvalidEmployeeIdFormatIsProvided() {
        //Given
        String employeeId = "invalidUUIDFormat";

        //When
        Map<String, Object> actual = restTemplate.getForEntity(employeeIdUrl, Map.class, employeeId).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/employee/invalidUUIDFormat", actual.get("path"));
    }

    @Test
    public void ensure400Response_whenUpdate_givenInvalidEmployeeIdFormatIsProvided() {
        //Given
        HttpHeaders headers = buildHttpHeadersWithJsonContentType();
        String employeeId = "invalidUUIDFormat";
        Employee employee = TestBuilderUtil.buildEmployee();

        //When
        Map<String, Object> actual = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT,
                new HttpEntity<>(employee, headers), Map.class, employeeId).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/employee/invalidUUIDFormat", actual.get("path"));
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = buildHttpHeadersWithJsonContentType();

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }
}
