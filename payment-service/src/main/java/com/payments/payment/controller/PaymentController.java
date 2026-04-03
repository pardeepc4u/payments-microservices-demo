package com.payments.payment.controller;

import com.payments.common.domain.PaymentStatus;
import com.payments.payment.dto.CreatePaymentRequest;
import com.payments.payment.dto.PaymentResponse;
import com.payments.payment.service.PaymentService;
import com.payments.common.util.CorrelationIdUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        String correlationId = CorrelationIdUtil.generate();
        CorrelationIdUtil.setCorrelationId(correlationId);
        
        try {
            PaymentResponse response = paymentService.createPayment(request, correlationId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .header(CorrelationIdUtil.CORRELATION_ID_HEADER, correlationId)
                .body(response);
        } finally {
            CorrelationIdUtil.clear();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String id) {
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getPayments(
            @RequestParam(required = false) String accountId,
            Pageable pageable) {
        Page<PaymentResponse> payments;
        if (accountId != null) {
            payments = paymentService.getPaymentsByAccount(accountId, pageable);
        } else {
            payments = paymentService.getPaymentsByAccount("all", pageable);
        }
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String id) {
        PaymentResponse response = paymentService.cancelPayment(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "payment-service"));
    }
}
