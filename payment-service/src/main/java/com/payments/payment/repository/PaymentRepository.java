package com.payments.payment.repository;

import com.payments.payment.domain.Payment;
import com.payments.common.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    List<Payment> findByFromAccountId(String fromAccountId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByCorrelationId(String correlationId);
    
    Optional<Payment> findByTraceNumber(String traceNumber);
    
    @Query("SELECT p FROM Payment p WHERE p.fromAccountId = :accountId OR p.toAccountId = :accountId")
    Page<Payment> findByAccountId(@Param("accountId") String accountId, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.status IN :statuses AND p.createdAt < :before")
    List<Payment> findByStatusInAndCreatedAtBefore(
        @Param("statuses") List<PaymentStatus> statuses, 
        @Param("before") java.time.Instant before
    );
}
