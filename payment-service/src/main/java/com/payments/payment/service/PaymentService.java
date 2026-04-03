package com.payments.payment.service;

import com.payments.common.domain.*;
import com.payments.common.exception.EntityNotFoundException;
import com.payments.common.exception.BusinessRuleException;
import com.payments.payment.domain.Payment;
import com.payments.payment.dto.CreatePaymentRequest;
import com.payments.payment.dto.PaymentResponse;
import com.payments.payment.kafka.PaymentEventProducer;
import com.payments.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer eventProducer;
    
    public PaymentService(PaymentRepository paymentRepository, PaymentEventProducer eventProducer) {
        this.paymentRepository = paymentRepository;
        this.eventProducer = eventProducer;
    }
    
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, String correlationId) {
        log.info("Creating payment from {} to {} for amount {} {}", 
            request.fromAccountId(), request.toAccountId(), request.amount(), request.currency());
        
        validatePaymentRequest(request);
        
        String paymentId = UUID.randomUUID().toString();
        String traceNumber = generateTraceNumber();
        
        Payment payment = new Payment(
            paymentId,
            request.paymentType(),
            request.fromAccountId(),
            request.toAccountId(),
            request.amount(),
            request.routingNumber(),
            maskAccountNumber(request.accountNumber()),
            request.description(),
            correlationId != null ? correlationId : UUID.randomUUID().toString()
        );
        payment.setTraceNumber(traceNumber);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
            savedPayment.getId(),
            savedPayment.getPaymentType(),
            savedPayment.getAmount(),
            savedPayment.getFromAccountId(),
            savedPayment.getToAccountId(),
            savedPayment.getRoutingNumber(),
            savedPayment.getAccountNumber(),
            savedPayment.getDescription(),
            savedPayment.getCorrelationId()
        );
        
        eventProducer.sendPaymentInitiated(event);
        
        log.info("Payment created with ID: {}", savedPayment.getId());
        return PaymentResponse.from(savedPayment);
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment", id));
        return PaymentResponse.from(payment);
    }
    
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByAccount(String accountId, Pageable pageable) {
        return paymentRepository.findByAccountId(accountId, pageable)
            .map(PaymentResponse::from);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
            .map(PaymentResponse::from)
            .toList();
    }
    
    @Transactional
    public PaymentResponse cancelPayment(String id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment", id));
        
        if (!payment.canCancel()) {
            throw new BusinessRuleException("Payment cannot be cancelled in current status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        Payment savedPayment = paymentRepository.save(payment);
        
        log.info("Payment {} cancelled", id);
        return PaymentResponse.from(savedPayment);
    }
    
    public void handleAccountUpdated(AccountUpdatedEvent event) {
        log.info("Processing account update for payment: {}", event.getPaymentId());
        
        paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.VALIDATED) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
                
                PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    payment.getId(),
                    payment.getPaymentType(),
                    payment.getAmount(),
                    payment.getFromAccountId(),
                    payment.getToAccountId(),
                    payment.getTraceNumber(),
                    payment.getCorrelationId()
                );
                eventProducer.sendPaymentCompleted(completedEvent);
            }
        });
    }
    
    public void processValidatedPayment(PaymentValidatedEvent event) {
        log.info("Processing validated payment: {}", event.getPaymentId());
        
        paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
        });
    }
    
    public void handleValidationFailure(PaymentValidatedEvent event) {
        log.info("Handling validation failure for payment: {}", event.getPaymentId());
        
        paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(event.getValidationMessage());
            paymentRepository.save(payment);
            
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                payment.getId(),
                payment.getPaymentType(),
                payment.getAmount(),
                payment.getFromAccountId(),
                payment.getToAccountId(),
                event.getValidationMessage(),
                "VALIDATION_FAILED",
                payment.getCorrelationId()
            );
            eventProducer.sendPaymentFailed(failedEvent);
        });
    }
    
    private void validatePaymentRequest(CreatePaymentRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BusinessRuleException("Source and destination accounts cannot be the same");
        }
        
        if (request.paymentType() == PaymentType.ACH && request.routingNumber() == null) {
            throw new BusinessRuleException("ACH payments require routing number");
        }
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
    
    private String generateTraceNumber() {
        return "TR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
