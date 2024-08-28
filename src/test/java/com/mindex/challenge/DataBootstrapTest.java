package com.mindex.challenge;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.model.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBootstrapTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void ensureEmployeeIsLocated_whenFindByEmployeeId_givenValidEmployeeId() {
        Optional<Employee> employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertTrue(employee.isPresent());
        assertEquals("John", employee.get().getFirstName());
        assertEquals("Lennon", employee.get().getLastName());
        assertEquals("Development Manager", employee.get().getPosition());
        assertEquals("Engineering", employee.get().getDepartment());
    }

    @Test
    public void ensureEmployeeIsNotLocated_whenFindByEmployeeId_givenInvalidEmployeeId() {
        Optional<Employee> employee = employeeRepository.findByEmployeeId("invalidEmployeeId");
        assertTrue(employee.isEmpty());
    }
}