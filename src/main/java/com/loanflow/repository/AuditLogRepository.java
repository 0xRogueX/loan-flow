package com.loanflow.repository;

import com.loanflow.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Example of a custom paginated query (if you want to filter by action in the future)
//    Page<AuditLog> findByAction(String action, Pageable pageable);

    // Admin view — paginated, most recent first
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
