package com.loanflow.repository;

import com.loanflow.entity.Notification;
import com.loanflow.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetryCount);
}
