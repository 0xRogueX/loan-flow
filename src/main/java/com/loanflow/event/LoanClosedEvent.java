package com.loanflow.event;

import com.loanflow.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoanClosedEvent {

    private final Loan loan;
}
