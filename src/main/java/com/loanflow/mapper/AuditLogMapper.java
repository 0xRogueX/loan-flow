package com.loanflow.mapper;

import com.loanflow.dto.response.AuditLogResponse;
import com.loanflow.entity.AuditLog;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    /**
     * performedBy is nullable — null when SYSTEM performed the action.
     * MapStruct null-safe: performedBy == null → performedByName = null.
     *
     * AuditLog does NOT extend BaseEntity, so id and createdAt
     * are declared directly on AuditLog — MapStruct finds them fine.
     *
     * changeDetailJson is a raw JSON String passed through as-is.
     * The admin UI is responsible for pretty-printing it.
     */
    @Mapping(target = "performedByName", source = "performedBy.name")
    AuditLogResponse toResponse(AuditLog auditLog);

    /** Used by AdminController.getAuditLogs() */
    List<AuditLogResponse> toResponseList(List<AuditLog> logs);
}

