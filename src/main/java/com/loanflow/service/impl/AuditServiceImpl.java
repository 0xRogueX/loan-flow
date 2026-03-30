package com.loanflow.service.impl;

import com.loanflow.dto.request.AuditRequest;
import com.loanflow.dto.response.AuditLogResponse;
import com.loanflow.entity.AuditLog;
import com.loanflow.entity.user.User;
import com.loanflow.mapper.AuditLogMapper;
import com.loanflow.repository.AuditLogRepository;
import com.loanflow.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    private final AuditLogMapper auditLogMapper;
    /**
     * Records every state transition in the system.
     * AuditLog.createdAt is set automatically at entity level.
     * performedBy is null when actorRole = SYSTEM.
     */
    public void log(AuditRequest request) {

        AuditLog entry = new AuditLog();
        entry.setEntityType(request.getEntityType());
        entry.setEntityId(request.getEntityId());
        entry.setAction(request.getAction());
        entry.setOldStatus(request.getOldStatus());
        entry.setNewStatus(request.getNewStatus());
        entry.setPerformedBy(request.getPerformedBy()); // null for SYSTEM
        entry.setActorRole(request.getActorRole());
        entry.setRemarks(request.getRemarks());

        auditLogRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllAuditLogs(Pageable pageable) {
        // Fetch the paginated entities from the database
        Page<AuditLog> auditLogPage = auditLogRepository.findAll(pageable);

        // Spring Data's Page object has a built-in .map() function to convert Entities to DTOs!
        return auditLogPage.map(auditLogMapper::toResponse);
    }
}
