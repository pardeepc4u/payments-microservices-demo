package com.payments.notification.repository;

import com.payments.notification.model.Notification;
import com.payments.notification.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);

    Page<Notification> findByUserId(String userId, Pageable pageable);

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);

    List<Notification> findByCreatedAtBetween(Instant start, Instant end);

    long countByStatus(NotificationStatus status);
}
