package com.loanflow.dto.response;
import com.loanflow.enums.ApplicationStatus;
import com.loanflow.enums.BureauStatus;
import com.loanflow.enums.LoanStrategy;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationResponse {

    private Long id;

    private String applicationNumber;

    private BigDecimal requestedAmount;

    private Integer tenureMonths;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyEmi;

    /** System-computed DTI at time of application */
    private BigDecimal calculatedDti;

    /** Auto-suggested by system — borrower cannot change this */
    private LoanStrategy suggestedStrategy;

    /** Officer's final strategy — may differ from suggested */
    private LoanStrategy finalStrategy;

    private ApplicationStatus status;

    private String rejectionReason;

//    /** Credit Bureau score at application time — shown to officer */
//    private Integer bureauScore;

    /** AVAILABLE or UNAVAILABLE */
    private BureauStatus bureauStatus;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    private String reviewedByName;
}



