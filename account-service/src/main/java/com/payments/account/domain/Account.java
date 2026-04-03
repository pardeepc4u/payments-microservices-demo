package com.payments.account.domain;

import com.payments.common.domain.BaseEntity;
import com.payments.common.domain.AccountStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {
    
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;
    
    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;
    
    @Column(name = "routing_number", length = 9)
    private String routingNumber;
    
    @Column(name = "available_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;
    
    @Column(name = "current_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Column(name = "held_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal heldAmount = BigDecimal.ZERO;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    protected Account() {
        super();
    }
    
    public Account(String id, String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        super(id);
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.currentBalance = initialBalance;
        this.availableBalance = initialBalance;
    }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { 
        this.availableBalance = availableBalance;
        markUpdated();
    }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { 
        this.currentBalance = currentBalance;
        markUpdated();
    }
    
    public BigDecimal getHeldAmount() { return heldAmount; }
    public void setHeldAmount(BigDecimal heldAmount) { 
        this.heldAmount = heldAmount;
        markUpdated();
    }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { 
        this.status = status;
        markUpdated();
    }
    
    public void holdFunds(BigDecimal amount) {
        this.availableBalance = this.availableBalance.subtract(amount);
        this.heldAmount = this.heldAmount.add(amount);
        markUpdated();
    }
    
    public void releaseFunds(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
        this.heldAmount = this.heldAmount.subtract(amount);
        markUpdated();
    }
    
    public void captureFunds(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        this.heldAmount = this.heldAmount.subtract(amount);
        markUpdated();
    }
    
    public boolean hasAvailableBalance(BigDecimal amount) {
        return this.availableBalance.compareTo(amount) >= 0;
    }
    
    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }
}
