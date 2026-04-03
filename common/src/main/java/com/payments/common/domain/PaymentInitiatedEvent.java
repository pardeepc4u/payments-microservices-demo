package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when a payment is initiated.
 */
public class PaymentInitiatedEvent extends PaymentEvent {
    
    private String routingNumber;
    private String accountNumber;
    private String description;
    
    public PaymentInitiatedEvent() {
        super();
    }
    
    public PaymentInitiatedEvent(String paymentId, PaymentType paymentType, BigDecimal amount,
                                  String fromAccountId, String toAccountId, String routingNumber,
                                  String accountNumber, String description, String correlationId) {
        super(paymentId, paymentType, PaymentStatus.INITIATED, amount, fromAccountId, toAccountId, correlationId);
        this.routingNumber = routingNumber;
        this.accountNumber = accountNumber;
        this.description = description;
    }
    
    public String getRoutingNumber() {
        return routingNumber;
    }
    
    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
