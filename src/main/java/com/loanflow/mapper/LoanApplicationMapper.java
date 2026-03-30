package com.loanflow.mapper;

import com.loanflow.dto.response.LoanApplicationResponse;
import com.loanflow.entity.LoanApplication;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    /**
     * reviewedBy is nullable — MapStruct null-safe chaining handles this.
     * If reviewedBy == null → reviewedByName = null (no NPE).
     *
     * monthlyIncome and existingMonthlyEmi are included so the officer
     * can see the borrower's declared income during review.
     */
    @Mapping(target = "reviewedByName", source = "reviewedBy.name")
    LoanApplicationResponse toResponse(LoanApplication application);

    /** Used by officer GET /api/v1/officer/applications */
    List<LoanApplicationResponse> toResponseList(List<LoanApplication> applications);
}
