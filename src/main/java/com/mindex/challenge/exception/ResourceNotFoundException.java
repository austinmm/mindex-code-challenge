package com.mindex.challenge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceNotFoundException.class);

    @Serial
    private static final long serialVersionUID = 1L;

    private ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException buildResourceNotFoundException(String message) {
        LOG.error(message);
        return new ResourceNotFoundException(message);
    }
}
