package com.mindex.challenge.controller;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.dto.ReportingStructure;
import com.mindex.challenge.model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.mindex.challenge.testingutils.TestAssertionUtil.assertEmployeeEquivalence;
import static com.mindex.challenge.testingutils.TestFunctionUtil.buildUrlWithPortAndPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureControllerTest {

    private String reportingStructureEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        reportingStructureEmployeeIdUrl = buildUrlWithPortAndPath(port, "/reporting-structure/{employeeId}");
    }

    @Test
    public void ensure400Response_whenFetchReportingStructureForEmployeeId_givenInvalidEmployeeIdFormatIsProvided() {
        //Given
        String employeeId = "invalidUUIDFormat";

        //When
        Map<String, Object> actual = restTemplate.getForEntity(reportingStructureEmployeeIdUrl, Map.class, employeeId).getBody();

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.get("status"));
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), actual.get("error"));
        assertEquals("/reporting-structure/invalidUUIDFormat", actual.get("path"));
    }

    /*
      If this was junit5 I would only have one unit test and make it an @ParameterizedTest
      I can't easily achieve this with junit4 because I can only use one @RunWith
    */
    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenJohnLennonEmployeeId() {
        //Given
        int expectedNumberOfReports = 4;
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        //When & Then
        executeAndValidateFetchReportingStructureForEmployeeIdAPI(employeeId, expectedNumberOfReports);
    }

    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenPaulMcCartneyEmployeeId() {
        //Given
        int expectedNumberOfReports = 0;
        String employeeId = "b7839309-3348-463b-a7e3-5de1c168beb3";

        //When & Then
        executeAndValidateFetchReportingStructureForEmployeeIdAPI(employeeId, expectedNumberOfReports);
    }

    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenRingoStarEmployeeId() {
        //Given
        int expectedNumberOfReports = 2;
        String employeeId = "03aa1462-ffa9-4978-901b-7c001562cf6f";

        //When & Then
        executeAndValidateFetchReportingStructureForEmployeeIdAPI(employeeId, expectedNumberOfReports);
    }

    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenPeteBestEmployeeId() {
        //Given
        int expectedNumberOfReports = 0;
        String employeeId = "62c1084e-6e34-4630-93fd-9153afb65309";

        //When & Then
        executeAndValidateFetchReportingStructureForEmployeeIdAPI(employeeId, expectedNumberOfReports);
    }

    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenGeorgeHarrisonEmployeeId() {
        //Given
        int expectedNumberOfReports = 0;
        String employeeId = "c0c2293d-16bd-4603-8e08-638a9d18b22c";

        //When & Then
        executeAndValidateFetchReportingStructureForEmployeeIdAPI(employeeId, expectedNumberOfReports);
    }

    private void executeAndValidateFetchReportingStructureForEmployeeIdAPI(String employeeId, int expectedNumberOfReports) {
        Employee expectedEmployee = employeeRepository.findByEmployeeId(employeeId).get();

        //When
        ReportingStructure actual = restTemplate.getForEntity(reportingStructureEmployeeIdUrl, ReportingStructure.class, employeeId).getBody();

        //Then
        assertNotNull(actual);
        assertNotNull(actual.getEmployee());
        assertEquals(employeeId, actual.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(expectedEmployee, actual.getEmployee());
        assertEquals(expectedNumberOfReports, actual.getNumberOfReports());
    }
}
