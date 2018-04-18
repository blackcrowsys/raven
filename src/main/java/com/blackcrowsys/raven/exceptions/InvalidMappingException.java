package com.blackcrowsys.raven.exceptions;

/**
 * Exception thrown when there is a problem with mapping - usually invalid mapping configuration.
 */
public class InvalidMappingException extends RuntimeException {

    public InvalidMappingException(String message) {
        super(message);
    }
}
