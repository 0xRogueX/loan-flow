package com.loanflow.service;

import com.loanflow.dto.request.LoanApplicationRequest;
import com.loanflow.dto.response.LoanApplicationResponse;
import com.loanflow.entity.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanApplicationService {
    @Transactional
    LoanApplicationResponse apply(LoanApplicationRequest request, User borrower);

    @Transactional
    void cancelApplication(String applicationNumber, User borrower);

    //  READ — officer pending queue
    @Transactional(readOnly = true)
    List<LoanApplicationResponse> getPendingApplications();

    @Transactional(readOnly = true)
    List<LoanApplicationResponse> getMyApplications(User borrower);
}
