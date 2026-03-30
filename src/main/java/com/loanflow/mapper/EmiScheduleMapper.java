package com.loanflow.mapper;

import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.entity.EmiSchedule;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmiScheduleMapper {

    /**
     * CRITICAL: id → emiScheduleId rename.
     * The borrower reads emiScheduleId from this response
     * and sends it in PaymentSimulationRequest to pay the installment.
     * Without this rename the API contract breaks.
     */
    @Mapping(target = "emiScheduleId", source = "id")
    EmiScheduleResponse toResponse(EmiSchedule emiSchedule);

    /**
     * Used by EmiScheduleService.getScheduleByLoan().
     * Returns the complete amortization table ordered by installmentNumber.
     */
    List<EmiScheduleResponse> toResponseList(List<EmiSchedule> schedules);
}
