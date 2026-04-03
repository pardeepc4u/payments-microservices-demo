package com.payments.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.common.domain.PaymentCompletedEvent;
import com.payments.common.domain.PaymentFailedEvent;
import com.payments.common.kafka.KafkaTopics;
import com.payments.notification.model.NotificationChannel;
import com.payments.notification.model.NotificationType;
import com.payments.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentCompleted(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            log.info("Received PaymentCompletedEvent: correlationId={}, paymentId={}",
                    event.getCorrelationId(), event.getPaymentId());

            notificationService.sendNotification(
                    com.payments.notification.dto.SendNotificationRequest.builder()
                            .correlationId(event.getCorrelationId())
                            .notificationType(NotificationType.PAYMENT_COMPLETED)
                            .channel(NotificationChannel.EMAIL)
                            .recipient("user@example.com")
                            .subject("Payment Completed Successfully")
                            .content(String.format(
                                    "Your payment of $%.2f has been completed successfully. " +
                                    "Payment ID: %s, Trace: %s",
                                    event.getAmount(), event.getPaymentId(), event.getTraceNumber()))
                            .build()
            );
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            log.info("Received PaymentFailedEvent: correlationId={}, paymentId={}",
                    event.getCorrelationId(), event.getPaymentId());

            notificationService.sendNotification(
                    com.payments.notification.dto.SendNotificationRequest.builder()
                            .correlationId(event.getCorrelationId())
                            .notificationType(NotificationType.PAYMENT_FAILED)
                            .channel(NotificationChannel.EMAIL)
                            .recipient("user@example.com")
                            .subject("Payment Failed")
                            .content(String.format(
                                    "Your payment of $%.2f has failed. Reason: %s. " +
                                    "Payment ID: %s",
                                    event.getAmount(), event.getFailureReason(), event.getPaymentId()))
                            .build()
            );
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent: {}", e.getMessage(), e);
        }
    }
}
