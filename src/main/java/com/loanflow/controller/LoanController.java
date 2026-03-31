package com.loanflow.controller;

import com.loanflow.constants.ApiConstants;
import com.loanflow.dto.response.ApiResponse;
import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.entity.Loan;
import com.loanflow.entity.user.User;
import com.loanflow.enums.Role;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.EmiScheduleService;
import com.loanflow.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.LOAN_BASE)
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;
    private final EmiScheduleService emiScheduleService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{loanNumber}" + ApiConstants.PATH_SCHEDULE)
    @PreAuthorize("hasAnyRole('BORROWER', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<List<EmiScheduleResponse>>> getEmiSchedule(
            @PathVariable String loanNumber) {

        User currentUser = securityUtils.getCurrentUser();
        log.debug("Fetching EMI schedule for loan {} by user {}", loanNumber, currentUser.getEmail());

        Loan loan = loanService.findByLoanNumber(loanNumber);

        if (currentUser.getRole() == Role.BORROWER
                && !loan.getBorrower().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only view your own loan schedules.");
        }

        List<EmiScheduleResponse> schedule = emiScheduleService.getScheduleByLoanNumber(loanNumber);

        return ResponseEntity.ok(ApiResponse.ok("EMI Schedule fetched successfully.", schedule));
    }
}
