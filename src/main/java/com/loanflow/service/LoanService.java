package com.loanflow.service;

import com.loanflow.dto.request.LoanDecisionRequest;
import com.loanflow.dto.response.LoanResponse;
import com.loanflow.entity.Loan;
import com.loanflow.entity.user.User;

import java.util.List;
import java.util.UUID;

public interface LoanService {

    // Officer processes loan application (approve/reject)
    LoanResponse processDecision(UUID applicationId,
                                 LoanDecisionRequest request,
                                 User officer);

    // Close loan when all EMIs are paid
    void closeLoanIfCompleted(Loan loan);

    // Get all loans for a borrower
    List<LoanResponse> getMyLoans(User borrower);

    // Find loan by ID
    Loan findById(UUID loanId);
}
