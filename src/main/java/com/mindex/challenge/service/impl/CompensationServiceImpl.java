package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dto.CompensationCreationRequest;
import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.model.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.mindex.challenge.exception.ResourceNotFoundException.buildResourceNotFoundException;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    private final CompensationRepository compensationRepository;
    private final EmployeeService employeeService;

    public CompensationServiceImpl(CompensationRepository compensationRepository, EmployeeService employeeService) {
        this.compensationRepository = compensationRepository;
        this.employeeService = employeeService;
    }

    @Override
    public Compensation createNewCompensationForEmployee(CompensationCreationRequest compensationCreationRequest) {
        //When creating a new compensation for an employee we will also store their current position and department for historical tracking
        Employee employee = employeeService.read(compensationCreationRequest.getEmployeeId());
        LOG.debug("Located employee to create new compensation for: {}", employee);
        Compensation compensation = buildCompensation(employee, compensationCreationRequest);
        LOG.debug("Creating compensation: {}", compensation);
        //We insert the employee's new compensation while preserving their previous compensation data
        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation fetchCurrentCompensationForEmployeeId(String employeeId) {
        LOG.debug("Reading compensation for employeeId: {}", employeeId);

        //Since an employee might have 1-N compensation records we only want to pull the one with the most recent effective date
        return compensationRepository.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)
                .orElseThrow(() -> buildResourceNotFoundException("Failed to fetch compensation for employeeId: " + employeeId));
    }

    private static Compensation buildCompensation(Employee employee, CompensationCreationRequest compensationCreationRequest) {
        Compensation compensation = new Compensation();
        compensation.setCompensationId(UUID.randomUUID().toString());
        compensation.setEmployeeId(compensationCreationRequest.getEmployeeId());
        compensation.setSalary(compensationCreationRequest.getSalary());
        compensation.setPosition(employee.getPosition());
        compensation.setDepartment(employee.getDepartment());
        compensation.setEffectiveDate(Instant.now().toString());
        return compensation;
    }
}
