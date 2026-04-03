package com.payments.common.exception;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessRuleException extends PaymentException {
    
    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }
}
