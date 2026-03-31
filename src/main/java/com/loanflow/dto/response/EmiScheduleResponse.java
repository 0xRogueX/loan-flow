package com.loanflow.dto.response;

import com.loanflow.enums.EmiStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiScheduleResponse {

    /** Send this ID in PaymentSimulationRequest to pay this installment */
    private Long emiScheduleId;

    private Integer installmentNumber;

    private LocalDate dueDate;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    /** principalAmount + interestAmount */
    private BigDecimal totalEmiAmount;

    /** Balance remaining after paying this installment */
    private BigDecimal remainingBalance;

    private EmiStatus status;

    private LocalDateTime paidAt;
}
