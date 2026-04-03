package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Base class for all payment-related events published to Kafka.
 */
public abstract class PaymentEvent {
    
    private String eventId;
    private String paymentId;
    private PaymentType paymentType;
    private PaymentStatus status;
    private BigDecimal amount;
    private String fromAccountId;
    private String toAccountId;
    private Instant timestamp;
    private String correlationId;
    
    protected PaymentEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }
    
    protected PaymentEvent(String paymentId, PaymentType paymentType, PaymentStatus status, 
                          BigDecimal amount, String fromAccountId, String toAccountId,
                          String correlationId) {
        this();
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.status = status;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.correlationId = correlationId;
    }
    
    // Getters and setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getFromAccountId() {
        return fromAccountId;
    }
    
    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    
    public String getToAccountId() {
        return toAccountId;
    }
    
    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
