package com.payments.audit.repository;

import com.payments.audit.model.AuditLog;
import com.payments.audit.model.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByCorrelationId(String correlationId, Pageable pageable);

    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    Page<AuditLog> findByEventType(EventType eventType, Pageable pageable);

    List<AuditLog> findByEntityIdAndEntityType(String entityId, String entityType);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    Page<AuditLog> findByTimestampBetween(
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    Page<AuditLog> findByUserIdAndTimestampBetween(
            @Param("userId") String userId,
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.eventType = :eventType AND a.timestamp >= :since")
    long countByEventTypeSince(@Param("eventType") EventType eventType, @Param("since") Instant since);

    @Query("SELECT a.eventType, COUNT(a) FROM AuditLog a WHERE a.timestamp >= :since GROUP BY a.eventType")
    List<Object[]> countByEventTypeGrouped(@Param("since") Instant since);
}
