package com.mindex.challenge.dto;

import com.mindex.challenge.model.Employee;

public class ReportingStructure {

    private final Employee employee;
    private final int numberOfReports;

    public ReportingStructure(Employee employee, int numberOfReports) {
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public Employee getEmployee() {
        return employee;
    }
}
