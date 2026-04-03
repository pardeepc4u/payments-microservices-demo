package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when a payment completes.
 */
public class PaymentCompletedEvent extends PaymentEvent {
    
    private String traceNumber;
    
    public PaymentCompletedEvent() {
        super();
    }
    
    public PaymentCompletedEvent(String paymentId, PaymentType paymentType, BigDecimal amount,
                                  String fromAccountId, String toAccountId, String traceNumber,
                                  String correlationId) {
        super(paymentId, paymentType, PaymentStatus.COMPLETED, amount, fromAccountId, toAccountId, correlationId);
        this.traceNumber = traceNumber;
    }
    
    public String getTraceNumber() {
        return traceNumber;
    }
    
    public void setTraceNumber(String traceNumber) {
        this.traceNumber = traceNumber;
    }
}
