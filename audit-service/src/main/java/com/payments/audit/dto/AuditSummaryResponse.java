package com.payments.audit.dto;

import com.payments.audit.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditSummaryResponse {

    private long totalLogs;
    private int periodDays;
    private String userId;
    private Map<EventType, Long> eventCounts;
    private Instant generatedAt;
}
