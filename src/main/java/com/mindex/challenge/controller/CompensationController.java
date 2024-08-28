package com.mindex.challenge.controller;

import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.service.CompensationService;
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
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request: {}", compensation);

        return compensationService.create(compensation);
    }

    @GetMapping("/{employeeId}")
    public Compensation read(@UUID @PathVariable String employeeId) {
        LOG.debug("Received compensation read request for employeeId: {}", employeeId);

        return compensationService.read(employeeId);
    }
}
