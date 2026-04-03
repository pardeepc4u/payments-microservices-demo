package com.payments.notification.dto;

import com.payments.notification.model.NotificationChannel;
import com.payments.notification.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    private String correlationId;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String subject;

    @NotBlank(message = "Content is required")
    private String content;
}
