package com.mindex.challenge.service;

import com.mindex.challenge.model.Compensation;

public interface CompensationService {

    Compensation create(Compensation compensation);

    Compensation read(String employeeId);
}
