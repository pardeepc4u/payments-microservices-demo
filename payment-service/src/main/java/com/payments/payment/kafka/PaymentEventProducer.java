package com.payments.payment.kafka;

import com.payments.common.domain.*;
import com.payments.common.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventProducer {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendPaymentInitiated(PaymentInitiatedEvent event) {
        send(KafkaTopics.PAYMENT_INITIATED, event.getPaymentId(), event);
    }
    
    public void sendPaymentValidated(PaymentValidatedEvent event) {
        send(KafkaTopics.PAYMENT_VALIDATED, event.getPaymentId(), event);
    }
    
    public void sendPaymentCompleted(PaymentCompletedEvent event) {
        send(KafkaTopics.PAYMENT_COMPLETED, event.getPaymentId(), event);
    }
    
    public void sendPaymentFailed(PaymentFailedEvent event) {
        send(KafkaTopics.PAYMENT_FAILED, event.getPaymentId(), event);
    }
    
    private void send(String topic, String key, Object event) {
        log.info("Sending event to topic {}: {}", topic, key);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send event to {}: {}", topic, ex.getMessage());
            } else {
                log.debug("Event sent to {} partition {} offset {}", 
                    topic, 
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            }
        });
    }
}
