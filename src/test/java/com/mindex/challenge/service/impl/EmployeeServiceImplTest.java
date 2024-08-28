package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.model.Employee;
import com.mindex.challenge.exception.ResourceNotFoundException;
import com.mindex.challenge.testingutils.TestBuilderUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.mindex.challenge.testingutils.TestAssertionUtil.assertEmployeeEquivalence;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    public void ensureEmployeeIsLocated_whenFindByEmployeeId_givenEmployeeIdHasMatchInDb() {
        //Given
        Employee expected = TestBuilderUtil.buildEmployee();
        String employeeId = expected.getEmployeeId();
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(expected));

        //When
        Employee actual = employeeServiceImpl.read(employeeId);

        //Then
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        assertEmployeeEquivalence(expected, actual);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ensureResourceNotFoundExceptionIsThrown_whenFindByEmployeeId_givenEmployeeIdHasNoMatchInDb() {
        //Given
        String employeeId = "employeeIdWithNoMatch";
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        //When
        employeeServiceImpl.read(employeeId);
    }

    @Test
    public void ensureEmployeeIsCreated_whenCreate_givenValidEmployee() {
        //Given
        Employee expected = TestBuilderUtil.buildEmployee();
        expected.setEmployeeId(null);
        when(employeeRepository.insert(any(Employee.class))).thenReturn(expected);

        //When
        Employee actual = employeeServiceImpl.create(expected);

        //Then
        verify(employeeRepository, times(1)).insert(any(Employee.class));
        assertNotNull(actual.getEmployeeId());
    }

    @Test
    public void ensureEmployeeIsUpdated_whenUpdate_givenValidEmployee() {
        //Given
        Employee expected = TestBuilderUtil.buildEmployee();
        when(employeeRepository.save(any(Employee.class))).thenReturn(expected);

        //When
        Employee actual = employeeServiceImpl.update(expected);

        //Then
        verify(employeeRepository, times(1)).save(any(Employee.class));
        assertEmployeeEquivalence(expected, actual);
    }
}
