package com.mindex.challenge.controller;

import com.mindex.challenge.exception.ResourceNotFoundException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RestControllerAdviceTest {

    private final RestControllerAdvice restControllerAdvice = new RestControllerAdvice();

    @Test
    public void ensure404ResponseBodyWithExceptionMessage_whenHandleException_givenResourceNotFoundException() {
        //Given
        String expectedMessage = "This is the expected message";
        ResourceNotFoundException exception = ResourceNotFoundException.buildResourceNotFoundException(expectedMessage);

        //When
        ResponseEntity<String> actual = restControllerAdvice.handleException(exception);

        //Then
        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(expectedMessage, actual.getBody());
    }
}
