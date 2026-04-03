package com.payments.notification.dto;

import com.payments.notification.model.NotificationChannel;
import com.payments.notification.model.NotificationStatus;
import com.payments.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String userId;
    private String correlationId;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private String recipient;
    private String subject;
    private String content;
    private NotificationStatus status;
    private Integer retryCount;
    private Instant createdAt;
    private Instant sentAt;
}
