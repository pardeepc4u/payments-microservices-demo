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
public class AuditSearchRequest {

    private String correlationId;
    private String userId;
    private EventType eventType;
    private Instant startDate;
    private Instant endDate;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;
}
