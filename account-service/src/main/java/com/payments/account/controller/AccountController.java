package com.payments.account.controller;

import com.payments.account.dto.AccountResponse;
import com.payments.account.dto.CreateAccountRequest;
import com.payments.account.dto.CreateHoldRequest;
import com.payments.account.dto.HoldResponse;
import com.payments.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    
    private final AccountService accountService;
    
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String id) {
        AccountResponse response = accountService.getAccount(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getBalance(@PathVariable String id) {
        AccountResponse response = accountService.getBalance(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/hold")
    public ResponseEntity<HoldResponse> createHold(@PathVariable String id, @Valid @RequestBody CreateHoldRequest request) {
        HoldResponse response = accountService.createHold(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{id}/release/{holdId}")
    public ResponseEntity<HoldResponse> releaseHold(@PathVariable String id, @PathVariable String holdId) {
        HoldResponse response = accountService.releaseHold(id, holdId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "account-service"));
    }
}
