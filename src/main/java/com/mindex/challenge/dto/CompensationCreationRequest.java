package com.mindex.challenge.dto;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.UUID;

public class CompensationCreationRequest {

    @UUID
    private String employeeId;

    @Min(0)
    private int salary;

    public CompensationCreationRequest(String employeeId, int salary) {
        this.employeeId = employeeId;
        this.salary = salary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public int getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "CompensationCreationRequest{" +
                "employeeId='" + employeeId + '\'' +
                ", salary=" + salary +
                '}';
    }
}
