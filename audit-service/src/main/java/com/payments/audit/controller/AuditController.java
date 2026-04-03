package com.payments.audit.controller;

import com.payments.audit.dto.AuditLogResponse;
import com.payments.audit.dto.AuditSearchRequest;
import com.payments.audit.dto.AuditSummaryResponse;
import com.payments.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getAuditLog(@PathVariable Long id) {
        AuditLogResponse response = auditService.getAuditLog(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AuditLogResponse>> searchAuditLogs(
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        AuditSearchRequest request = AuditSearchRequest.builder()
                .correlationId(correlationId)
                .userId(userId)
                .eventType(eventType != null ? com.payments.audit.model.EventType.valueOf(eventType) : null)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        Page<AuditLogResponse> results = auditService.searchAuditLogs(request);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/summary")
    public ResponseEntity<AuditSummaryResponse> getAuditSummary(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "7") int days) {
        AuditSummaryResponse summary = auditService.getAuditSummary(userId, days);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "audit-service"));
    }
}
