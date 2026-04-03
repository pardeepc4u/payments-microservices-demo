package com.payments.payment.kafka;

import com.payments.common.domain.*;
import com.payments.common.kafka.KafkaTopics;
import com.payments.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    
    private final PaymentService paymentService;
    
    public PaymentEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @KafkaListener(
        topics = KafkaTopics.ACCOUNT_UPDATED,
        groupId = "payment-service-account-updates",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAccountUpdated(AccountUpdatedEvent event) {
        log.info("Received account updated event for payment: {}", event.getPaymentId());
        if (event.getPaymentId() != null) {
            paymentService.handleAccountUpdated(event);
        }
    }
    
    @KafkaListener(
        topics = KafkaTopics.PAYMENT_VALIDATED,
        groupId = "payment-service-validation",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentValidated(PaymentValidatedEvent event) {
        log.info("Received payment validated event: {}", event.getPaymentId());
        if (event.isValidationSuccessful()) {
            paymentService.processValidatedPayment(event);
        } else {
            paymentService.handleValidationFailure(event);
        }
    }
}
