package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.model.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.mindex.challenge.exception.ResourceNotFoundException.buildResourceNotFoundException;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    private final CompensationRepository compensationRepository;

    public CompensationServiceImpl(CompensationRepository compensationRepository) {
        this.compensationRepository = compensationRepository;
    }

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation: {}", compensation);
        compensationRepository.insert(compensation);
        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employeeId: {}", employeeId);

        return compensationRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> buildResourceNotFoundException("Failed to read compensation for employeeId: " + employeeId));
    }
}
