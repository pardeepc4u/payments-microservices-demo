package com.payments.common.exception;

/**
 * Exception thrown when a requested entity is not found.
 */
public class EntityNotFoundException extends PaymentException {
    
    public EntityNotFoundException(String entityType, String id) {
        super("NOT_FOUND", entityType + " not found with id: " + id);
    }
}
