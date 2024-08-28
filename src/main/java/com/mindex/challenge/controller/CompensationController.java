package com.mindex.challenge.controller;

import com.mindex.challenge.dto.CompensationCreationRequest;
import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.service.CompensationService;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compensation")
public class CompensationController {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    private final CompensationService compensationService;

    public CompensationController(CompensationService compensationService) {
        this.compensationService = compensationService;
    }

    @PostMapping
    public Compensation createNewCompensationForEmployee(@Valid @RequestBody CompensationCreationRequest compensationCreationRequest) {
        LOG.debug("Received compensation creation request: {}", compensationCreationRequest);
        return compensationService.createNewCompensationForEmployee(compensationCreationRequest);
    }

    @GetMapping("/{employeeId}")
    public Compensation fetchCurrentCompensationForEmployeeId(@UUID @PathVariable String employeeId) {
        LOG.debug("Received request to fetch current compensation for employeeId: {}", employeeId);
        return compensationService.fetchCurrentCompensationForEmployeeId(employeeId);
    }
}
