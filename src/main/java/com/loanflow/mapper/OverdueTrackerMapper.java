package com.loanflow.mapper;

import com.loanflow.dto.response.OverdueTrackerResponse;
import com.loanflow.entity.OverdueTracker;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OverdueTrackerMapper {

    /**
     * loan.id             → loanId
     * emiSchedule.id      → emiScheduleId
     * borrower.name       → borrowerName    (denormalised for officer view)
     * borrower.email      → borrowerEmail
     *
     * borrower is User (abstract) — name and email live on User parent,
     * so MapStruct accesses them without any casting.
     */
    @Mapping(target = "loanId",        source = "loan.id")
    @Mapping(target = "emiScheduleId",  source = "emiSchedule.id")
    @Mapping(target = "borrowerName",   source = "borrower.name")
    @Mapping(target = "borrowerEmail",  source = "borrower.email")
    OverdueTrackerResponse toResponse(OverdueTracker tracker);

    /** Used by ReportService.getOverdueSummary() detail list */
    List<OverdueTrackerResponse> toResponseList(List<OverdueTracker> trackers);
}

