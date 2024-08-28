package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.dto.CompensationCreationRequest;
import com.mindex.challenge.exception.ResourceNotFoundException;
import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.model.Employee;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.testingutils.TestBuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CompensationServiceImplTest {

    private CompensationServiceImpl compensationServiceImpl;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompensationRepository compensationRepository;

    @Captor
    private ArgumentCaptor<Compensation> compensationCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);
        compensationServiceImpl = new CompensationServiceImpl(compensationRepository, employeeService);
    }

    @Test
    public void ensureCompensationIsCreated_whenCreateNewCompensationForEmployee_givenValidCompensationCreationRequest() {
        //Given
        Instant beforeCreationTime = Instant.now();
        Employee employee = TestBuilderUtil.buildEmployee();
        String employeeId = employee.getEmployeeId();
        int expectedSalary = 100;
        CompensationCreationRequest compensationCreationRequest = new CompensationCreationRequest(employeeId, expectedSalary);
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(compensationRepository.insert(any(Compensation.class))).thenReturn(new Compensation());

        //When
        compensationServiceImpl.createNewCompensationForEmployee(compensationCreationRequest);

        //Then
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(compensationRepository, times(1)).insert(compensationCaptor.capture());
        Compensation actual = compensationCaptor.getValue();

        assertNotNull(actual);
        assertEquals(compensationCreationRequest.getEmployeeId(), actual.getEmployeeId());
        assertEquals(compensationCreationRequest.getSalary(), actual.getSalary());
        assertEquals(employee.getDepartment(), actual.getDepartment());
        assertEquals(employee.getPosition(), actual.getPosition());
        assertNotNull(actual.getEffectiveDate());
        assertTrue(Instant.parse(actual.getEffectiveDate()).isAfter(beforeCreationTime));
        assertTrue(Instant.parse(actual.getEffectiveDate()).isBefore(Instant.now()));
    }

    @Test
    public void ensureValidCompensation_whenFetchCurrentCompensationForEmployeeId_givenValidEmployeeId() {
        //Given
        Employee employee = TestBuilderUtil.buildEmployee();
        String employeeId = employee.getEmployeeId();
        Compensation expected = buildCompensationForEmployee(employee);
        when(compensationRepository.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)).thenReturn(Optional.of(expected));

        //When
        Compensation actual = compensationServiceImpl.fetchCurrentCompensationForEmployeeId(employeeId);

        //Then
        verify(compensationRepository, times(1)).findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId);

        assertNotNull(actual);
        assertEquals(expected.getCompensationId(), actual.getCompensationId());
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ensureResourceNotFoundExceptionIsThrown_whenCreateNewCompensationForEmployee_givenEmployeeIdInCompensationCreationRequestHasNotMatchInDb() {
        //Given
        String employeeId = "employeeIdWithNoMatchInDb";
        CompensationCreationRequest compensationCreationRequest = new CompensationCreationRequest(employeeId, 100);
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        //When
        compensationServiceImpl.createNewCompensationForEmployee(compensationCreationRequest);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ensureResourceNotFoundExceptionIsThrown_whenFetchCurrentCompensationForEmployeeId_givenEmployeeIdHasNotMatchInDb() {
        //Given
        String employeeId = "employeeIdWithNoMatchInDb";
        when(compensationRepository.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)).thenReturn(Optional.empty());

        //When
        compensationServiceImpl.fetchCurrentCompensationForEmployeeId(employeeId);
    }

    private static Compensation buildCompensationForEmployee(Employee employee) {
        Compensation compensation = new Compensation();
        compensation.setCompensationId(UUID.randomUUID().toString());
        compensation.setEmployeeId(employee.getEmployeeId());
        compensation.setPosition(employee.getPosition());
        compensation.setDepartment(employee.getDepartment());
        compensation.setEffectiveDate(Instant.now().toString());
        compensation.setSalary(100);
        return compensation;
    }
}
