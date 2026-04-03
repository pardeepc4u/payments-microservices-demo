package com.payments.settlement.repository;

import com.payments.settlement.model.SettlementEntry;
import com.payments.settlement.model.SettlementEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementEntryRepository extends JpaRepository<SettlementEntry, Long> {

    List<SettlementEntry> findByBatchId(Long batchId);

    List<SettlementEntry> findByStatus(SettlementEntryStatus status);

    List<SettlementEntry> findByPaymentId(String paymentId);

    @Query("SELECT e FROM SettlementEntry e WHERE e.batch.id = :batchId AND e.status = :status")
    List<SettlementEntry> findByBatchIdAndStatus(
            @Param("batchId") Long batchId,
            @Param("status") SettlementEntryStatus status);

    @Query("SELECT COUNT(e) FROM SettlementEntry e WHERE e.batch.id = :batchId")
    long countByBatchId(@Param("batchId") Long batchId);
}
