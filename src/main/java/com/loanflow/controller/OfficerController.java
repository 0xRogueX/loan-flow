package com.loanflow.controller;


import com.loanflow.dto.request.LoanDecisionRequest;
import com.loanflow.dto.response.*;
import com.loanflow.entity.user.User;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.LoanApplicationService;
import com.loanflow.service.LoanService;
import com.loanflow.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/officer")
@RequiredArgsConstructor
public class OfficerController {

    private final LoanApplicationService loanApplicationService;
    private final LoanService loanService;
    private final ReportService reportService;
    private final SecurityUtils securityUtils;

    @GetMapping("/applications")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getApplications(){

        return ResponseEntity.ok(ApiResponse.ok(loanApplicationService.getPendingApplications()));
    }


    @PutMapping("/approve/{applicationNumber}")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanResponse>> decide(
            @PathVariable String applicationNumber,
            @Valid @RequestBody LoanDecisionRequest request) {
        User officer = securityUtils.getCurrentUser();

        LoanResponse result = loanService.processDecision(applicationNumber, request, officer);
        String message = result != null ? "Loan application approved." : "Loan application rejected.";

        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    @GetMapping("/reports/overdue")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<OverdueSummaryResponse>> overdueSummary() {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.getOverdueSummary()));
    }

    @GetMapping("/reports/portfolio")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanPortfolioResponse>> portfolio() {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.getPortfolioSummary()));
    }


}
