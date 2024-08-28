package com.mindex.challenge.controller;

import com.mindex.challenge.model.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.hibernate.validator.constraints.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/*
  NOTE:
  I would recommend that the Employee DB POJO class not be sent/received to/from the client.
  Instead, I would recommend the use of a data transfer object (DTO) to reduce the overhead
    if the requirements for the client's Employee POJO eventually changes from the DB's Employee POJO schema.
*/

@RestController
@RequestMapping("employee")
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/{id}")
    public Employee read(@UUID @PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }

    @PutMapping("/{id}")
    public Employee update(@UUID @PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
}
