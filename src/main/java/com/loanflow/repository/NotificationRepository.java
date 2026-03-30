package com.loanflow.repository;

import com.loanflow.entity.Notification;
import com.loanflow.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetryCount);
}
