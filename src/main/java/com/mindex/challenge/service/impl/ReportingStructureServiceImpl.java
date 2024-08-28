package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    public ReportingStructureServiceImpl(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ReportingStructure generateReportingStructureForEmployeeId(String employeeId) {
        LOG.debug("Calculating the number of subordinates for employeeId: {}", employeeId);
        Employee employee = employeeService.read(employeeId);
        int numberOfReports = getNumberOfSubordinatesForEmployeeId(employeeId);
        return new ReportingStructure(employee, numberOfReports);
    }

    private int getNumberOfSubordinatesForEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Failed to locate employee, with id %s, when attempting to locate their subordinates", employeeId);
                    return new NullPointerException(errorMessage);
                });


        List<Employee> directReports = employee.getDirectReports();
        if (CollectionUtils.isEmpty(directReports)) {
            // This employee has no subordinates and has already been accounted for so we return 0 to exit the recursion
            return 0;
        }

        /*
         Recursively invokes the method for each direct report of queried employee.
         We then sum up all the recursive calls with an initial value of the queried employee's direct report count
        */
        return directReports.stream()
                .map(Employee::getEmployeeId)
                .map(this::getNumberOfSubordinatesForEmployeeId)
                .reduce(directReports.size(), Integer::sum);
    }
}
