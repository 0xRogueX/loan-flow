package com.loanflow.service.impl;

import com.loanflow.constants.LoanConstants;
import com.loanflow.dto.request.AuditRequest;
import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.entity.OverdueTracker;
import com.loanflow.enums.*;
import com.loanflow.event.OverdueAlertEvent;
import com.loanflow.repository.EmiScheduleRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.repository.OverdueTrackerRepository;
import com.loanflow.service.AuditService;
import com.loanflow.service.OverdueMonitorService;
import com.loanflow.util.DateUtil;
import com.loanflow.util.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverdueMonitorServiceImpl implements OverdueMonitorService {

    private final EmiScheduleRepository emiScheduleRepository;
    private final OverdueTrackerRepository overdueTrackerRepository;
    private final LoanRepository loanRepository;
    private final LoanStatusTransitionServiceImpl loanStatusTransitionService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;


    //  MAIN SCAN — called daily by OverdueScheduler at 1AM
    @Override
    @Transactional
    public void scanAndMarkOverdue() {

        LocalDate today = LocalDate.now();
        log.info("Starting daily overdue scan for {}", today);

        // find all PENDING EMIs with due passed
        List<EmiSchedule> overdueEmis = emiScheduleRepository
                .findByStatusAndDueDateBefore(EmiStatus.PENDING, today);
        log.info("Overdue scan: found {} missed EMIs", overdueEmis.size());

        for (EmiSchedule emi : overdueEmis) {

            String oldStatus = emi.getStatus().name();  // PENDING

            // Mark EMI as overdue
            emi.setStatus(EmiStatus.OVERDUE);
            emiScheduleRepository.save(emi);

            // create or update OverdueTracker
            OverdueTracker tracker = overdueTrackerRepository
                    .findByEmiSchedule(emi)
                    .orElse(new OverdueTracker());

            boolean isNewTracker = tracker.getDetectedAt() == null;

            tracker.setEmiSchedule(emi);
            tracker.setLoan(emi.getLoan());
            tracker.setBorrower(emi.getLoan().getBorrower());
            tracker.setDueDate(emi.getDueDate());
            tracker.setDaysOverdue(DateUtil.daysBetween(emi.getDueDate(), today));

            // set detectedAt only first time
            if (isNewTracker) {
                tracker.setDetectedAt(LocalDateTime.now());
            }

            applyPenalty(tracker, emi);
            overdueTrackerRepository.save(tracker);

            // increment loan's overdue count and check for default
            Loan loan = emi.getLoan();
            loan.setOverDueCount(loan.getOverDueCount() + 1);
            loanRepository.save(loan);

            // check loan should transition to DEFAULTED
            if (tracker.getDaysOverdue() >= LoanConstants.DEFAULT_THRESHOLD_DAYS
                    && loan.getStatus() == LoanStatus.ACTIVE) {

                loanStatusTransitionService.transition(
                        loan, LoanStatus.DEFAULTED,
                        null, "Loan defaulted after " + loan.getOverDueCount() + " overdue days"
                );
                loanRepository.save(loan);
                log.warn("Loan {} transitioned to DEFAULTED", loan.getId());
            }

            // send overdue alert once (on first detection only)
            if (tracker.getAlertCount() == 0) {
                eventPublisher.publishEvent(new OverdueAlertEvent(tracker));
                tracker.setAlertCount(1);
                tracker.setLastAlertAt(LocalDateTime.now());
                overdueTrackerRepository.save(tracker);
                log.info("Overdue alert sent for EMI {} of loan {}",
                        emi.getInstallmentNumber(), loan.getId());
            }

            // audit log with correct old status
            auditService.log(AuditRequest.builder()
                            .entityType(EntityType.EMI_SCHEDULE)
                            .entityId(emi.getId())
                            .action("MARK_OVERDUE")
                            .oldStatus(oldStatus)
                            .newStatus(EmiStatus.OVERDUE.name())
                            .performedBy(null)  // system action
//                            .actorRole(LoanConstants.ACTOR_SYSTEM)
                            .remarks("Days overdue: " + tracker.getDaysOverdue()
                                    + " | Penalty: ₹" + tracker.getFixedPenaltyAmount()
                                    + " | Daily charge: ₹" + tracker.getPenaltyCharge())
                            .build());

            log.info("EMI {} marked OVERDUE — loan {}, days overdue: {}",
                    emi.getId(), emi.getLoan().getLoanNumber(), tracker.getDaysOverdue());

        }
    }

    //  WRITTEN_OFF SCAN — called daily by OverdueScheduler
    //  Loans that have been DEFAULTED for 180+ days -> WRITTEN_OFF
    @Override
    @Transactional
    public void scanAndMarkWrittenOff() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusDays(LoanConstants.WRITTEN_OFF_DAYS);

        // Find loans that have been DEFAULTED since before the cutoff
        List<Loan> defaultedLoans = loanRepository
                .findByStatusAndUpdatedAtBefore(LoanStatus.DEFAULTED, cutoff);

        log.info("Written-off scan: found {} loans defaulted for {}+ days",
                defaultedLoans.size(), LoanConstants.WRITTEN_OFF_DAYS);

        for (Loan loan : defaultedLoans) {
            loanStatusTransitionService.transition(
                    loan, LoanStatus.WRITTEN_OFF, null,
                    "Loan written off after " + LoanConstants.WRITTEN_OFF_DAYS
                            + " days of DEFAULTED status");
            loanRepository.save(loan);

            log.warn("Loan {} written off — defaulted since {}",
                    loan.getId(), loan.getUpdatedAt());

            auditService.log(AuditRequest.builder()
                    .entityType(EntityType.LOAN)
                    .entityId(loan.getId())
                    .action("WRITTEN_OFF")
                    .oldStatus(LoanStatus.DEFAULTED.name())
                    .newStatus(LoanStatus.WRITTEN_OFF.name())
                    .performedBy(null)
                    .actorRole(Role.valueOf(LoanConstants.ACTOR_SYSTEM))
                    .remarks("Auto written-off after "
                            + LoanConstants.WRITTEN_OFF_DAYS + " days")
                    .build());
        }
    }

    //  PENALTY CALCULATION — called per tracker per scan

    /**
     * Three-stage penalty logic:
     *
     * Stage 1 — Day 1 (first detection):
     *   fixedPenaltyAmount = ₹500 flat fee
     *   penaltyStatus      = APPLIED
     *
     * Stage 2 — Day 1 to 29 (grace period):
     *   fixedPenaltyAmount stays at ₹500
     *   penaltyCharge = 0 (no daily charge yet)
     *
     * Stage 3 — Day 30+ (daily accumulation):
     *   penaltyCharge = penaltyRate × daysOverdue × remainingBalance
     *   penaltyRate   = LoanConstants.OVERDUE_DAILY_PENALTY_RATE (0.05%)
     *
     * Total penalty borrower owes = fixedPenaltyAmount + penaltyCharge
     */
    private void applyPenalty(OverdueTracker tracker, EmiSchedule emi) {

        Integer daysOverdue = tracker.getDaysOverdue();

        // Stage 1: flat fee on first detection
        if (tracker.getFixedPenaltyAmount() == null || tracker.getFixedPenaltyAmount().compareTo(BigDecimal.ZERO) == 0) {
            tracker.setFixedPenaltyAmount(LoanConstants.LATE_FEE_FLAT_AMOUNT);
            tracker.setPenaltyRate(LoanConstants.OVERDUE_DAILY_PENALTY_RATE);
            tracker.setPenaltyStatus(PenaltyStatus.APPLIED);
            log.debug("Fixed penalty applied for EMI {}: ₹{}",
                    emi.getInstallmentNumber(), LoanConstants.LATE_FEE_FLAT_AMOUNT);
        }

        // Stage 2: grace period, no daily charge yet
        if (daysOverdue <= LoanConstants.PENALTY_GRACE_DAYS) {
            tracker.setPenaltyCharge(BigDecimal.ZERO);
            log.debug("Within grace period for EMI {}: no daily penalty charge",
                    emi.getInstallmentNumber());
        }

        // Stage 3: daily penalty applies after grace period
        if (daysOverdue > LoanConstants.PENALTY_GRACE_DAYS) {

            BigDecimal totalPenaltyCharge = MoneyUtil.roundHalfUp(  // aaj tak ki penalty charge, not included fix penalty charge
                    LoanConstants.OVERDUE_DAILY_PENALTY_RATE
                            .multiply(BigDecimal.valueOf(daysOverdue))
                            .multiply(emi.getRemainingBalance())
            );

            tracker.setPenaltyCharge(totalPenaltyCharge);
            log.debug("Total upto penalty applied for EMI {}: Rs.{} ({} days overdue)",
                    emi.getInstallmentNumber(), totalPenaltyCharge, daysOverdue);
        }
    }
}
