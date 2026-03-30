package com.loanflow.mapper;

import com.loanflow.dto.response.LoanStatusHistoryResponse;
import com.loanflow.entity.LoanStatusHistory;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanStatusHistoryMapper {

    /**
     * changedBy is nullable (null when SYSTEM performed the transition).
     * MapStruct null-safe chaining: changedBy == null → changedByName = null.
     * UI renders 'System' when changedByName is null.
     */
    @Mapping(target = "changedByName", source = "changedBy.name")
    LoanStatusHistoryResponse toResponse(LoanStatusHistory history);

    /** Used in loan detail view — full transition timeline */
    List<LoanStatusHistoryResponse> toResponseList(List<LoanStatusHistory> histories);
}

