package com.loanflow.service;

import com.loanflow.entity.Loan;
import com.loanflow.entity.user.User;
import com.loanflow.enums.LoanStatus;

public interface LoanStatusTransitionService {

    void transition(Loan loan, LoanStatus newStatus,User changeBy, String reason);
}
