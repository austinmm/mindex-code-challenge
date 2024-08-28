package com.mindex.challenge.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceNotFoundExceptionTest {

    @Test
    public void ensureResourceNotFoundExceptionIsCreated_whenBuildResourceNotFoundException_givenMessage() {
        //Given
        String expectedMessage = "This is the expected message";

        //When
        ResourceNotFoundException actual = ResourceNotFoundException.buildResourceNotFoundException(expectedMessage);

        //Then
        assertNotNull(actual);
        assertEquals(expectedMessage, actual.getMessage());
    }
}
