package com.payments.settlement.repository;

import com.payments.settlement.model.SettlementBatch;
import com.payments.settlement.model.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementBatchRepository extends JpaRepository<SettlementBatch, Long> {

    Page<SettlementBatch> findByStatus(SettlementStatus status, Pageable pageable);

    @Query("SELECT s FROM SettlementBatch s WHERE s.batchDate BETWEEN :start AND :end ORDER BY s.batchDate DESC")
    Page<SettlementBatch> findByBatchDateBetween(
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable);

    Optional<SettlementBatch> findFirstByStatusOrderByCreatedAtDesc(SettlementStatus status);

    List<SettlementBatch> findByStatusIn(List<SettlementStatus> statuses);

    @Query("SELECT COUNT(s) FROM SettlementBatch s WHERE s.status = :status")
    long countByStatus(@Param("status") SettlementStatus status);
}
