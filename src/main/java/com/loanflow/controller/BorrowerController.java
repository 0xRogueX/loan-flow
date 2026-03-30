package com.loanflow.controller;

import com.loanflow.dto.request.LoanApplicationRequest;
import com.loanflow.dto.response.ApiResponse;
import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.dto.response.LoanApplicationResponse;
import com.loanflow.dto.response.LoanResponse;
import com.loanflow.entity.Loan;
import com.loanflow.entity.user.User;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.EmiScheduleService;
import com.loanflow.service.LoanApplicationService;
import com.loanflow.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/borrower")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('BORROWER')")
public class BorrowerController {

    private final LoanApplicationService loanApplicationService;
    private final LoanService loanService;
    private final EmiScheduleService emiScheduleService;
    private final SecurityUtils securityUtils;


    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> applyLoan(
            @Valid @RequestBody LoanApplicationRequest request) {

        User borrower = securityUtils.getCurrentUser();

        log.info("Received loan application request from borrower: {}", borrower.getEmail());

        LoanApplicationResponse response = loanApplicationService.apply(request, borrower);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Loan application submitted successfully.", response));
    }

    /**
     * POST /api/v1/borrower/applications
     * Submits a new loan application.
     */
//    @PostMapping("/applications")
//    public ResponseEntity<ApiResponse<LoanApplicationResponse>> applyLoan(
//            @Valid @RequestBody LoanApplicationRequest request) {
//
//
//        /** after authentication **/
//        User borrower = securityUtils.getCurrentUser();
//        log.info("Received loan application request from borrower: {}", borrower.getEmail());
//
//        LoanApplicationResponse response = loanApplicationService.apply(request, borrower);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.created("Loan application submitted successfully.", response));
//    }

    /**
     * PUT /api/v1/borrower/applications/{applicationNumber}/cancel
     * Cancels an existing PENDING or UNDER_REVIEW application.
     */
    @PutMapping("/applications/{applicationNumber}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable String applicationNumber) {

        User borrower = securityUtils.getCurrentUser();
        log.info("Borrower {} requested to cancel application: {}", borrower.getEmail(), applicationNumber);

        loanApplicationService.cancelApplication(applicationNumber, borrower);

        return ResponseEntity.ok(ApiResponse.ok("Application cancelled successfully.", null));
    }

    /**
     * GET /api/v1/borrower/applications
     * Retrieves all loan applications submitted by the logged-in borrower.
     */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getMyApplications() {

        User borrower = securityUtils.getCurrentUser();
        List<LoanApplicationResponse> applications = loanApplicationService.getMyApplications(borrower);

        return ResponseEntity.ok(ApiResponse.ok("Applications fetched successfully.", applications));
    }

    /**
     * GET /api/v1/borrower/loans
     * Retrieves all active, closed, or defaulted loans belonging to the logged-in borrower.
     */
    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans() {

        User borrower = securityUtils.getCurrentUser();
        List<LoanResponse> loans = loanService.getMyLoans(borrower);

        return ResponseEntity.ok(ApiResponse.ok("Loans fetched successfully.", loans));
    }

    /**
     * GET /api/v1/borrower/loans/{loanNumber}/schedule
     * Retrieves the complete EMI amortization schedule using the business key (loanNumber).
     */
    @GetMapping("/loans/{loanNumber}/schedule")
    public ResponseEntity<ApiResponse<List<EmiScheduleResponse>>> getEmiSchedule(
            @PathVariable String loanNumber) {

        User borrower = securityUtils.getCurrentUser();
        log.debug("Fetching EMI schedule for loan {} by borrower {}", loanNumber, borrower.getEmail());

        // Ownership check: verify this loan belongs to the authenticated borrower
        Loan loan = loanService.findByLoanNumber(loanNumber);
        if (!loan.getBorrower().getId().equals(borrower.getId())) {
            throw new UnauthorizedAccessException("You can only view your own loan schedule.");
        }

        List<EmiScheduleResponse> schedule = emiScheduleService.getScheduleByLoanNumber(loanNumber);

        return ResponseEntity.ok(ApiResponse.ok("EMI Schedule fetched successfully.", schedule));
    }
}