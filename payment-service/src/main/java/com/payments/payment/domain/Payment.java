package com.payments.payment.domain;

import com.payments.common.domain.BaseEntity;
import com.payments.common.domain.PaymentStatus;
import com.payments.common.domain.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;
    
    @Column(name = "from_account_id", nullable = false, length = 36)
    private String fromAccountId;
    
    @Column(name = "to_account_id", nullable = false, length = 36)
    private String toAccountId;
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";
    
    @Column(name = "routing_number", length = 9)
    private String routingNumber;
    
    @Column(name = "account_number", length = 17)
    private String accountNumber;
    
    @Column(name = "trace_number", length = 15)
    private String traceNumber;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "correlation_id", length = 36)
    private String correlationId;
    
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
    
    protected Payment() {
        super();
        this.status = PaymentStatus.INITIATED;
    }
    
    public Payment(String id, PaymentType paymentType, String fromAccountId, String toAccountId,
                   BigDecimal amount, String routingNumber, String accountNumber, String description,
                   String correlationId) {
        super(id);
        this.paymentType = paymentType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.routingNumber = routingNumber;
        this.accountNumber = accountNumber;
        this.description = description;
        this.correlationId = correlationId;
        this.status = PaymentStatus.INITIATED;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        markUpdated();
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
        markUpdated();
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
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
    
    public String getTraceNumber() {
        return traceNumber;
    }
    
    public void setTraceNumber(String traceNumber) {
        this.traceNumber = traceNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public boolean canCancel() {
        return status == PaymentStatus.INITIATED || status == PaymentStatus.VALIDATED;
    }
}
