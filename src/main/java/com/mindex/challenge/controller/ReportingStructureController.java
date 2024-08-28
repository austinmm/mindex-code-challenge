package com.mindex.challenge.controller;

import com.mindex.challenge.dto.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.hibernate.validator.constraints.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reporting-structure")
public class ReportingStructureController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    private final ReportingStructureService reportingStructureService;

    public ReportingStructureController(ReportingStructureService reportingStructureService) {
        this.reportingStructureService = reportingStructureService;
    }

    @GetMapping("/{employeeId}")
    public ReportingStructure fetchReportingStructureForEmployeeId(@UUID @PathVariable String employeeId) {
        LOG.debug("Received request to fetch reporting structure for employeeId: {}", employeeId);
        return reportingStructureService.generateReportingStructureForEmployeeId(employeeId);
    }
}
