package com.payments.common.domain;

/**
 * Payment types supported by the system.
 */
public enum PaymentType {
    /**
     * Automated Clearing House - batch electronic payment
     */
    ACH,
    
    /**
     * Wire transfer - real-time gross settlement
     */
    WIRE
}
