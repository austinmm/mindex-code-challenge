package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.model.Employee;
import com.mindex.challenge.dto.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.testingutils.TestBuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.mindex.challenge.testingutils.TestAssertionUtil.assertEmployeeEquivalence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ReportingStructureServiceImplTest {

    private ReportingStructureServiceImpl reportingStructureServiceImpl;

    @Mock
    private EmployeeRepository employeeRepository;

    private final int numberOfLevels;
    private final int numberOfSubordinatesPerLevel;

    public ReportingStructureServiceImplTest(int numberOfLevels, int numberOfSubordinatesPerLevel) {
        this.numberOfLevels = numberOfLevels;
        this.numberOfSubordinatesPerLevel = numberOfSubordinatesPerLevel;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][]{{0, 0}, {1, 1}, {1, 3}, {2, 2}, {3, 4}});
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);
        reportingStructureServiceImpl = new ReportingStructureServiceImpl(employeeService, employeeRepository);
    }

    @Test
    public void ensureValidReportingStructure_whenGenerateReportingStructureForEmployeeId_givenValidEmployee() {
        //Given
        int expectedNumberOfReports = calculateTotalNumberOfExpectedSubordinates();//(int) Math.pow(numberOfSubordinatesPerLevel, numberOfLevels + 1) - 2;
        Employee headEmployee = TestBuilderUtil.buildEmployee();
        String employeeId = headEmployee.getEmployeeId();
        addSubordinatesUnderEmployee(headEmployee, numberOfLevels);
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(headEmployee));

        //When
        ReportingStructure actual = reportingStructureServiceImpl.generateReportingStructureForEmployeeId(employeeId);

        //Then
        assertNotNull(actual);
        assertNotNull(actual.getEmployee());
        assertEmployeeEquivalence(headEmployee, actual.getEmployee());
        assertEquals(expectedNumberOfReports, actual.getNumberOfReports());

        verify(employeeRepository, times(1 + expectedNumberOfReports)).findByEmployeeId(anyString());
    }

    private void addSubordinatesUnderEmployee(Employee headEmployee, int levelsRemaining) {
        if (levelsRemaining == 0) {
            return;
        }
        // Creates an m-ary tree of subordinates employees through recursion
        for (int i = 0; i < numberOfSubordinatesPerLevel; i++) {
            Employee employee = buildRandomEmployee();
            addSubordinatesUnderEmployee(employee, levelsRemaining - 1);
            headEmployee.getDirectReports().add(employee);
            when(employeeRepository.findByEmployeeId(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        }
    }

    private int calculateTotalNumberOfExpectedSubordinates() {
        if (numberOfLevels == 0) {
            //If no subordinate levels then there are no subordinates
            return 0;
        }

        if (numberOfLevels == 1) {
            //If one subordinate level then there are exactly `numberOfSubordinatesPerLevel` subordinates
            return numberOfSubordinatesPerLevel;
        }

        /*
        The recursive addSubordinatesUnderEmployee creates an m-ary tree of employees all under the root node, head employee.
        Therefore, the algorithm below calculates the total number of nodes in the m-ary tree,
            with a -1 at the end to exclude the head employee from being counted as a subordinate
        */
        return (int) ((Math.pow(numberOfSubordinatesPerLevel, numberOfLevels + 1) - 1) / (numberOfSubordinatesPerLevel - 1)) - 1;
    }

    private static Employee buildRandomEmployee() {
        String employeeId = UUID.randomUUID().toString();
        Employee employee = TestBuilderUtil.buildEmployee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName("First_" + employeeId);
        employee.setLastName("Last_" + employeeId);
        employee.setDirectReports(new ArrayList<>());
        return employee;
    }
}
