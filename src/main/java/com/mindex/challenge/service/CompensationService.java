package com.mindex.challenge.service;

import com.mindex.challenge.dto.CompensationCreationRequest;
import com.mindex.challenge.model.Compensation;

public interface CompensationService {

    Compensation createNewCompensationForEmployee(CompensationCreationRequest compensationCreationRequest);

    Compensation fetchCurrentCompensationForEmployeeId(String employeeId);
}
