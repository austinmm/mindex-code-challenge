package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.hibernate.validator.constraints.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reporting-structure")
public class ReportingStructureController {

    private final ReportingStructureService reportingStructureService;

    public ReportingStructureController(ReportingStructureService reportingStructureService) {
        this.reportingStructureService = reportingStructureService;
    }

    @GetMapping("/{employeeId}")
    public ReportingStructure fetchReportingStructureForEmployeeId(@UUID @PathVariable String employeeId) {
        return reportingStructureService.generateReportingStructureForEmployeeId(employeeId);
    }
}
