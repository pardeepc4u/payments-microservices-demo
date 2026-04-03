package com.payments.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.audit.dto.AuditLogResponse;
import com.payments.audit.dto.AuditSearchRequest;
import com.payments.audit.dto.AuditSummaryResponse;
import com.payments.audit.model.AuditLog;
import com.payments.audit.model.EventType;
import com.payments.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public AuditLog logEvent(EventType eventType, String serviceName, String correlationId,
                            String userId, String entityId, String entityType,
                            Object payload, Map<String, Object> metadata) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .eventType(eventType)
                    .serviceName(serviceName)
                    .correlationId(correlationId)
                    .userId(userId)
                    .entityId(entityId)
                    .entityType(entityType)
                    .payload(payload != null ? objectMapper.writeValueAsString(payload) : null)
                    .metadata(metadata != null ? objectMapper.writeValueAsString(metadata) : null)
                    .timestamp(Instant.now())
                    .build();

            auditLog = auditLogRepository.save(auditLog);
            log.info("Audit log created: id={}, eventType={}, correlationId={}",
                    auditLog.getId(), eventType, correlationId);
            return auditLog;
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }

    public AuditLogResponse getAuditLog(Long id) {
        return auditLogRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Audit log not found: " + id));
    }

    public Page<AuditLogResponse> searchAuditLogs(AuditSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<AuditLog> logs;

        if (request.getCorrelationId() != null) {
            logs = auditLogRepository.findByCorrelationId(request.getCorrelationId(), pageRequest);
        } else if (request.getUserId() != null) {
            logs = auditLogRepository.findByUserId(request.getUserId(), pageRequest);
        } else if (request.getEventType() != null) {
            logs = auditLogRepository.findByEventType(request.getEventType(), pageRequest);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            Instant start = request.getStartDate();
            Instant end = request.getEndDate();
            if (request.getUserId() != null) {
                logs = auditLogRepository.findByUserIdAndTimestampBetween(
                        request.getUserId(), start, end, pageRequest);
            } else {
                logs = auditLogRepository.findByTimestampBetween(start, end, pageRequest);
            }
        } else {
            logs = auditLogRepository.findAll(pageRequest);
        }

        return logs.map(this::mapToResponse);
    }

    public AuditSummaryResponse getAuditSummary(String userId, int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);

        long totalLogs = auditLogRepository.count();
        List<Object[]> eventCounts = auditLogRepository.countByEventTypeGrouped(since);

        Map<EventType, Long> eventTypeCounts = eventCounts.stream()
                .collect(Collectors.toMap(
                        row -> (EventType) row[0],
                        row -> (Long) row[1]
                ));

        return AuditSummaryResponse.builder()
                .totalLogs(totalLogs)
                .periodDays(days)
                .userId(userId)
                .eventCounts(eventTypeCounts)
                .generatedAt(Instant.now())
                .build();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .eventType(log.getEventType())
                .serviceName(log.getServiceName())
                .correlationId(log.getCorrelationId())
                .userId(log.getUserId())
                .entityId(log.getEntityId())
                .entityType(log.getEntityType())
                .payload(log.getPayload())
                .metadata(log.getMetadata())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .action(log.getAction())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .timestamp(log.getTimestamp())
                .build();
    }
}
