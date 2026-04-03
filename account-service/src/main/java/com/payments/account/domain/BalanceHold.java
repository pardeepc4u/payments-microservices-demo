package com.payments.account.domain;

import com.payments.common.domain.BaseEntity;
import com.payments.common.domain.HoldStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "balance_holds")
public class BalanceHold extends BaseEntity {
    
    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_id", length = 36)
    private String paymentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HoldStatus status = HoldStatus.PENDING;
    
    protected BalanceHold() {
        super();
    }
    
    public BalanceHold(String id, String accountId, BigDecimal amount, String paymentId) {
        super(id);
        this.accountId = accountId;
        this.amount = amount;
        this.paymentId = paymentId;
    }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public HoldStatus getStatus() { return status; }
    public void setStatus(HoldStatus status) { 
        this.status = status;
        markUpdated();
    }
}
