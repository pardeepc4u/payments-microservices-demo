package com.payments.common.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when a payment validation completes.
 */
public class PaymentValidatedEvent extends PaymentEvent {
    
    private boolean validationSuccessful;
    private String validationMessage;
    
    public PaymentValidatedEvent() {
        super();
    }
    
    public PaymentValidatedEvent(String paymentId, PaymentType paymentType, BigDecimal amount,
                                  String fromAccountId, String toAccountId, boolean validationSuccessful,
                                  String validationMessage, String correlationId) {
        super(paymentId, paymentType, PaymentStatus.VALIDATED, amount, fromAccountId, toAccountId, correlationId);
        this.validationSuccessful = validationSuccessful;
        this.validationMessage = validationMessage;
    }
    
    public boolean isValidationSuccessful() {
        return validationSuccessful;
    }
    
    public void setValidationSuccessful(boolean validationSuccessful) {
        this.validationSuccessful = validationSuccessful;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }
    
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
}
