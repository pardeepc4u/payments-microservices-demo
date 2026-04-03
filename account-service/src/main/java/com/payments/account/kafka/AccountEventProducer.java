package com.payments.account.kafka;

import com.payments.common.domain.AccountUpdatedEvent;
import com.payments.common.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class AccountEventProducer {
    
    private static final Logger log = LoggerFactory.getLogger(AccountEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public AccountEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendAccountUpdated(AccountUpdatedEvent event) {
        log.info("Sending account updated event: {}", event.getAccountId());
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(KafkaTopics.ACCOUNT_UPDATED, event.getAccountId(), event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send account updated event: {}", ex.getMessage());
            }
        });
    }
}
