package com.payments.account.service;

import com.payments.account.domain.Account;
import com.payments.account.domain.BalanceHold;
import com.payments.account.dto.AccountResponse;
import com.payments.account.dto.CreateAccountRequest;
import com.payments.account.dto.CreateHoldRequest;
import com.payments.account.dto.HoldResponse;
import com.payments.account.kafka.AccountEventProducer;
import com.payments.account.repository.AccountRepository;
import com.payments.account.repository.BalanceHoldRepository;
import com.payments.common.domain.AccountStatus;
import com.payments.common.domain.AccountUpdatedEvent;
import com.payments.common.domain.HoldStatus;
import com.payments.common.domain.PaymentInitiatedEvent;
import com.payments.common.exception.BusinessRuleException;
import com.payments.common.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {
    
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    
    private final AccountRepository accountRepository;
    private final BalanceHoldRepository holdRepository;
    private final AccountEventProducer eventProducer;
    
    public AccountService(AccountRepository accountRepository, 
                        BalanceHoldRepository holdRepository,
                        AccountEventProducer eventProducer) {
        this.accountRepository = accountRepository;
        this.holdRepository = holdRepository;
        this.eventProducer = eventProducer;
    }
    
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for: {}", request.accountHolderName());
        
        String accountNumber = generateAccountNumber();
        String id = UUID.randomUUID().toString();
        
        Account account = new Account(id, accountNumber, request.accountHolderName(), 
            request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO);
        account.setRoutingNumber(request.routingNumber());
        
        Account saved = accountRepository.save(account);
        log.info("Account created: {}", saved.getId());
        
        return AccountResponse.from(saved);
    }
    
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Account", id));
        return AccountResponse.from(account);
    }
    
    @Transactional(readOnly = true)
    public AccountResponse getBalance(String id) {
        return getAccount(id);
    }
    
    @Transactional
    public HoldResponse createHold(String accountId, CreateHoldRequest request) {
        log.info("Creating hold on account {} for amount {}", accountId, request.amount());
        
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Account", accountId));
        
        if (!account.isActive()) {
            throw new BusinessRuleException("Cannot create hold on inactive account");
        }
        
        if (!account.hasAvailableBalance(request.amount())) {
            throw new BusinessRuleException("Insufficient available balance");
        }
        
        String holdId = UUID.randomUUID().toString();
        BalanceHold hold = new BalanceHold(holdId, accountId, request.amount(), request.paymentId());
        
        account.holdFunds(request.amount());
        accountRepository.save(account);
        holdRepository.save(hold);
        
        emitAccountUpdated(account, "HOLD_CREATED", request.paymentId());
        
        log.info("Hold created: {}", holdId);
        return HoldResponse.from(hold);
    }
    
    @Transactional
    public HoldResponse releaseHold(String accountId, String holdId) {
        log.info("Releasing hold {} on account {}", holdId, accountId);
        
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Account", accountId));
        
        BalanceHold hold = holdRepository.findById(holdId)
            .orElseThrow(() -> new EntityNotFoundException("BalanceHold", holdId));
        
        if (hold.getStatus() != HoldStatus.PENDING) {
            throw new BusinessRuleException("Hold is not in PENDING status");
        }
        
        account.releaseFunds(hold.getAmount());
        hold.setStatus(HoldStatus.RELEASED);
        
        accountRepository.save(account);
        holdRepository.save(hold);
        
        emitAccountUpdated(account, "HOLD_RELEASED", hold.getPaymentId());
        
        log.info("Hold released: {}", holdId);
        return HoldResponse.from(hold);
    }
    
    public void handlePaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Processing payment initiated: {}", event.getPaymentId());
        
        accountRepository.findById(event.getFromAccountId()).ifPresent(account -> {
            if (account.hasAvailableBalance(event.getAmount())) {
                BigDecimal previousBalance = account.getAvailableBalance();
                account.holdFunds(event.getAmount());
                accountRepository.save(account);
                
                String holdId = UUID.randomUUID().toString();
                BalanceHold hold = new BalanceHold(holdId, account.getId(), 
                    event.getAmount(), event.getPaymentId());
                holdRepository.save(hold);
                
                emitAccountUpdated(account, "FUNDS_RESERVED", event.getPaymentId());
            }
        });
    }
    
    private void emitAccountUpdated(Account account, String eventType, String paymentId) {
        AccountUpdatedEvent event = new AccountUpdatedEvent(
            account.getId(),
            eventType,
            account.getCurrentBalance(),
            account.getAvailableBalance(),
            account.getHeldAmount(),
            paymentId,
            null
        );
        eventProducer.sendAccountUpdated(event);
    }
    
    private String generateAccountNumber() {
        return String.format("%010d", System.currentTimeMillis() % 1000000000L);
    }
}
