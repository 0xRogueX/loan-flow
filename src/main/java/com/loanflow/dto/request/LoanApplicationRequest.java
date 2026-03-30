package com.loanflow.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoanApplicationRequest {

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMax(value = "50000000", message = "Amount exceeds maximum limit")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1,   message = "Minimum tenure is 1 month")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be positive")
    private BigDecimal monthlyIncome;

//    /** Self-declared external EMI obligations (outside this system) */
//    @NotNull(message = "Existing EMI amount is required")
//    @PositiveOrZero(message = "Existing EMI cannot be negative")
//    private BigDecimal existingMonthlyEmi;
}