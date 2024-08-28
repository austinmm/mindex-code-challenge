package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.model.Employee;
import com.mindex.challenge.exception.ResourceNotFoundException;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.testingutils.TestBuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class ReportingStructureServiceImplEdgeCaseTest {

    private ReportingStructureServiceImpl reportingStructureServiceImpl;

    @Mock
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);
        reportingStructureServiceImpl = new ReportingStructureServiceImpl(employeeService, employeeRepository);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ensureResourceNotFoundExceptionIsThrown_whenGenerateReportingStructureForEmployeeId_givenEmployeeIdWithNoMatchInDb() {
        //Given
        String employeeId = "employeeIdWithNoMatchInDb";
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        //When
        reportingStructureServiceImpl.generateReportingStructureForEmployeeId(employeeId);
    }

    @Test(expected = NullPointerException.class)
    public void ensureNullPointerExceptionIsThrown_whenGenerateReportingStructureForEmployeeId_givenSubordinatesEmployeeIdWithNoMatchInDb() {
        //Given
        Employee headEmployee = TestBuilderUtil.buildEmployee();
        Employee subordinateEmployee = new Employee();
        subordinateEmployee.setEmployeeId(UUID.randomUUID().toString());
        headEmployee.getDirectReports().add(subordinateEmployee);

        when(employeeRepository.findByEmployeeId(headEmployee.getEmployeeId())).thenReturn(Optional.of(headEmployee));
        when(employeeRepository.findByEmployeeId(subordinateEmployee.getEmployeeId())).thenReturn(Optional.empty());

        //When
        reportingStructureServiceImpl.generateReportingStructureForEmployeeId(headEmployee.getEmployeeId());
    }
}
