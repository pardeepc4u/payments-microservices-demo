package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when an account balance changes.
 */
public class AccountUpdatedEvent {
    
    private String eventId;
    private String accountId;
    private String eventType;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private BigDecimal heldAmount;
    private String paymentId;
    private String correlationId;
    private Instant timestamp;
    
    public AccountUpdatedEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }
    
    public AccountUpdatedEvent(String accountId, String eventType, BigDecimal previousBalance,
                                BigDecimal newBalance, BigDecimal heldAmount, String paymentId,
                                String correlationId) {
        this();
        this.accountId = accountId;
        this.eventType = eventType;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.heldAmount = heldAmount;
        this.paymentId = paymentId;
        this.correlationId = correlationId;
    }
    
    // Getters and setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }
    
    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }
    
    public BigDecimal getNewBalance() {
        return newBalance;
    }
    
    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }
    
    public BigDecimal getHeldAmount() {
        return heldAmount;
    }
    
    public void setHeldAmount(BigDecimal heldAmount) {
        this.heldAmount = heldAmount;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
