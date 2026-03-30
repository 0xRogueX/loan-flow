package com.loanflow.event;

import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanApplication;
import com.loanflow.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoanDecisionEvent {

    private final Loan loan;
    private final LoanApplication application;
    private final ApplicationStatus decision;
    private final String rejectionReason;
}
