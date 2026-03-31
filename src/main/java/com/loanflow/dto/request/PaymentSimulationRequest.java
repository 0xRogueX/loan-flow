package com.loanflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentSimulationRequest{

    /** ID of the EmiSchedule installment to mark as paid.
     *  Obtained from GET /api/v1/loans/{loanNumber}/schedule response. */
    @NotNull(message = "EMI Schedule ID is required")
    private Long emiScheduleId;

}

