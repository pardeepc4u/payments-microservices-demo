package com.payments.audit.dto;

import com.payments.audit.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private EventType eventType;
    private String serviceName;
    private String correlationId;
    private String userId;
    private String entityId;
    private String entityType;
    private String payload;
    private String metadata;
    private String ipAddress;
    private String userAgent;
    private String action;
    private String status;
    private String errorMessage;
    private Instant timestamp;
}
