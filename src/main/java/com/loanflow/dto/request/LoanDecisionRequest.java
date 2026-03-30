package com.loanflow.dto.request;

import com.loanflow.enums.LoanStrategy;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoanDecisionRequest {

    @NotNull(message = "Approval decision is required")
    private Boolean approved;

    // Officer may override system-suggested strategy
    private LoanStrategy overrideStrategy;

    // Required when approved = true
    @Positive(message = "Interest rate must be positive")
    @DecimalMax(value = "36.0", message = "Interest rate exceeds maximum allowed")
    private BigDecimal interestRatePerAnnum;

    //Required when approved = false
    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    private String rejectionReason;
}

