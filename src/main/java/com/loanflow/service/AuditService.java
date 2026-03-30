package com.loanflow.service;

import com.loanflow.dto.request.AuditRequest;
import com.loanflow.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AuditService {

    void log(AuditRequest build);

    @Transactional(readOnly = true)
    Page<AuditLogResponse> getAllAuditLogs(Pageable pageable);
}
