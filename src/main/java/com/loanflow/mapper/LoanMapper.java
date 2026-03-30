package com.loanflow.mapper;

import com.loanflow.dto.response.LoanResponse;
import com.loanflow.entity.Loan;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    /**
     * approvedBy is always non-null on a Loan entity.
     * application is always non-null on a Loan entity.
     * closedAt maps as null until PaymentService sets it.
     */
    @Mapping(target = "approvedByName", source = "approvedBy.name")
    @Mapping(target = "applicationId",  source = "application.id")
    LoanResponse toResponse(Loan loan);

    /** Used by borrower GET /api/v1/loans — returns all borrower loans */
    List<LoanResponse> toResponseList(List<Loan> loans);
}
