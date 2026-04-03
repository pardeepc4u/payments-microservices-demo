package com.payments.account.kafka;

import com.payments.common.domain.PaymentInitiatedEvent;
import com.payments.common.kafka.KafkaTopics;
import com.payments.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final AccountService accountService;
    
    public PaymentEventConsumer(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @KafkaListener(
        topics = KafkaTopics.PAYMENT_INITIATED,
        groupId = "account-service-payments",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received payment initiated event: {}", event.getPaymentId());
        accountService.handlePaymentInitiated(event);
    }
}
