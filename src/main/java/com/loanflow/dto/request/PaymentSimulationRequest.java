package com.loanflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentSimulationRequest{

    /** ID of the EmiSchedule installment to mark as paid.
     *  Obtained from GET /api/v1/loans/{loanId}/schedule response. */
    @NotNull(message = "EMI Schedule ID is required")
    private UUID emiScheduleId;

}

