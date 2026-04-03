package com.payments.common.domain;

/**
 * Payment status lifecycle.
 */
public enum PaymentStatus {
    /**
     * Payment has been initiated but not yet validated
     */
    INITIATED,
    
    /**
     * Payment has passed validation checks
     */
    VALIDATED,
    
    /**
     * Payment is being processed by the payment network
     */
    PROCESSING,
    
    /**
     * Payment has completed successfully
     */
    COMPLETED,
    
    /**
     * Payment has failed
     */
    FAILED,
    
    /**
     * Payment has been returned (ACH)
     */
    RETURNED,
    
    /**
     * Payment has been cancelled
     */
    CANCELLED
}
