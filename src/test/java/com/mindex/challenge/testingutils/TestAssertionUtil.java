package com.mindex.challenge.testingutils;

import com.mindex.challenge.model.Employee;

import static org.junit.Assert.assertEquals;

public class TestAssertionUtil {

    public static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
