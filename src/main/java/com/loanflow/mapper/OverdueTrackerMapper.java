package com.loanflow.mapper;

import com.loanflow.dto.response.OverdueTrackerResponse;
import com.loanflow.entity.OverdueTracker;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OverdueTrackerMapper {

    @Mapping(target = "loanId",        source = "loan.id")
    @Mapping(target = "emiScheduleId",  source = "emiSchedule.id")
    @Mapping(target = "borrowerName",   source = "borrower.name")
    @Mapping(target = "borrowerEmail",  source = "borrower.email")
    @Mapping(target = "penaltySettled", expression = "java(tracker.getPenaltyStatus() == com.loanflow.enums.PenaltyStatus.SETTLED)")
    OverdueTrackerResponse toResponse(OverdueTracker tracker);

    List<OverdueTrackerResponse> toResponseList(List<OverdueTracker> trackers);
}

