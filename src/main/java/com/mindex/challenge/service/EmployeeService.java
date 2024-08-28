package com.mindex.challenge.service;

import com.mindex.challenge.model.Employee;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee read(String id);

    Employee update(Employee employee);
}
