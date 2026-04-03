package com.payments.notification.service;

import com.payments.notification.dto.NotificationResponse;
import com.payments.notification.dto.SendNotificationRequest;
import com.payments.notification.model.Notification;
import com.payments.notification.model.NotificationChannel;
import com.payments.notification.model.NotificationStatus;
import com.payments.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .correlationId(request.getCorrelationId())
                .notificationType(request.getNotificationType())
                .channel(request.getChannel())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .content(request.getContent())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .createdAt(Instant.now())
                .build();

        notification = notificationRepository.save(notification);

        try {
            switch (request.getChannel()) {
                case EMAIL -> sendEmail(notification);
                case SMS -> sendSms(notification);
                case PUSH -> sendPush(notification);
            }

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
        }

        notification.setUpdatedAt(Instant.now());
        notification = notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    private void sendEmail(Notification notification) {
        if (mailSender != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getContent());
            mailSender.send(message);
            log.info("Email sent to {} for notification {}", notification.getRecipient(), notification.getId());
        } else {
            log.info("Email notification {} would be sent to {}: {}",
                    notification.getId(), notification.getRecipient(), notification.getSubject());
        }
    }

    private void sendSms(Notification notification) {
        log.info("SMS notification {} would be sent to {}: {}",
                notification.getId(), notification.getRecipient(), notification.getContent());
    }

    private void sendPush(Notification notification) {
        log.info("Push notification {} would be sent to user {}: {}",
                notification.getId(), notification.getUserId(), notification.getContent());
    }

    public NotificationResponse getNotification(Long id) {
        return notificationRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .correlationId(notification.getCorrelationId())
                .notificationType(notification.getNotificationType())
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .status(notification.getStatus())
                .retryCount(notification.getRetryCount())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}
