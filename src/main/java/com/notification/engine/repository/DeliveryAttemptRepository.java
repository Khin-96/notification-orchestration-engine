package com.notification.engine.repository;

import com.notification.engine.model.DeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, Long> {
    List<DeliveryAttempt> findByNotificationIdOrderByAttemptedAtDesc(String notificationId);
}
