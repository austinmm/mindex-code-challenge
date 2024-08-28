package com.mindex.challenge.controller;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static com.mindex.challenge.testingutils.TestAssertionUtil.assertEmployeeEquivalence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureControllerControllerTest {

    private String reportingStructureEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        reportingStructureEmployeeIdUrl = "http://localhost:" + port + "/reporting-structure/{employeeId}";
    }

    @Test
    public void ensureValidReportingStructure_whenFetchReportingStructureForEmployeeId_givenValidEmployeeId() {
        //Given
        int expectedNumberOfReports = 4;
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
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
