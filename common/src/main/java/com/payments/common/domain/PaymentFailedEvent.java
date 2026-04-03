package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when a payment fails.
 */
public class PaymentFailedEvent extends PaymentEvent {
    
    private String failureReason;
    private String errorCode;
    
    public PaymentFailedEvent() {
        super();
    }
    
    public PaymentFailedEvent(String paymentId, PaymentType paymentType, BigDecimal amount,
                               String fromAccountId, String toAccountId, String failureReason,
                               String errorCode, String correlationId) {
        super(paymentId, paymentType, PaymentStatus.FAILED, amount, fromAccountId, toAccountId, correlationId);
        this.failureReason = failureReason;
        this.errorCode = errorCode;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
