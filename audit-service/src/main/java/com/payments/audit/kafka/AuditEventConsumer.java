package com.payments.audit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.audit.model.EventType;
import com.payments.audit.service.AuditService;
import com.payments.common.domain.PaymentCompletedEvent;
import com.payments.common.domain.PaymentFailedEvent;
import com.payments.common.domain.PaymentInitiatedEvent;
import com.payments.common.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PAYMENT_INITIATED, groupId = "audit-service-group")
    public void handlePaymentInitiated(String message) {
        try {
            PaymentInitiatedEvent event = objectMapper.readValue(message, PaymentInitiatedEvent.class);
            log.info("Audit: Payment initiated - correlationId={}, paymentId={}",
                    event.getCorrelationId(), event.getPaymentId());

            auditService.logEvent(
                    EventType.PAYMENT_INITIATED,
                    "payment-service",
                    event.getCorrelationId(),
                    null,
                    event.getPaymentId(),
                    "Payment",
                    objectMapper.readValue(message, Map.class),
                    null
            );
        } catch (Exception e) {
            log.error("Error processing payment initiated event for audit: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "audit-service-group")
    public void handlePaymentCompleted(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            log.info("Audit: Payment completed - correlationId={}, paymentId={}",
                    event.getCorrelationId(), event.getPaymentId());

            auditService.logEvent(
                    EventType.PAYMENT_COMPLETED,
                    "payment-service",
                    event.getCorrelationId(),
                    null,
                    event.getPaymentId(),
                    "Payment",
                    objectMapper.readValue(message, Map.class),
                    null
            );
        } catch (Exception e) {
            log.error("Error processing payment completed event for audit: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "audit-service-group")
    public void handlePaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            log.info("Audit: Payment failed - correlationId={}, paymentId={}",
                    event.getCorrelationId(), event.getPaymentId());

            auditService.logEvent(
                    EventType.PAYMENT_FAILED,
                    "payment-service",
                    event.getCorrelationId(),
                    null,
                    event.getPaymentId(),
                    "Payment",
                    objectMapper.readValue(message, Map.class),
                    Map.of("failureReason", event.getFailureReason())
            );
        } catch (Exception e) {
            log.error("Error processing payment failed event for audit: {}", e.getMessage(), e);
        }
    }
}
