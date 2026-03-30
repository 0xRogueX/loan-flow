package com.loanflow.service.impl;

import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanStatusHistory;
import com.loanflow.entity.user.User;
import com.loanflow.enums.LoanStatus;
import com.loanflow.exception.InvalidStatusTransitionException;
import com.loanflow.repository.LoanStatusHistoryRepository;
import com.loanflow.service.LoanStatusTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanStatusTransitionServiceImpl implements LoanStatusTransitionService {

    private final LoanStatusHistoryRepository loanStatusHistoryRepository;

    private static final Map<LoanStatus, Set<LoanStatus>> VALID_TRANSITIONS = Map.of(
            LoanStatus.ACTIVE,      Set.of(LoanStatus.CLOSED, LoanStatus.DEFAULTED),
            LoanStatus.DEFAULTED,   Set.of(LoanStatus.ACTIVE,  LoanStatus.WRITTEN_OFF),
            LoanStatus.CLOSED,      Set.of(),   // terminal
            LoanStatus.WRITTEN_OFF, Set.of()    // terminal
    );

    @Override
    public void transition(
            Loan loan, LoanStatus newStatus,
            User changedBy, String reason) {

        Set<LoanStatus> allowed = VALID_TRANSITIONS.getOrDefault(loan.getStatus(), Set.of());

        if (!allowed.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot transition Loan from " + loan.getStatus()
                            + " to " + newStatus
                            + ". Allowed: " + allowed);
        }

        LoanStatus oldStatus = loan.getStatus();
        loan.setStatus(newStatus);

        LoanStatusHistory history = LoanStatusHistory.builder()
                .loan(loan)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)    // null when system
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .build();

        loanStatusHistoryRepository.save(history);

        log.info("Loan {} transitioned: {} → {} by {}",
                loan.getId(), oldStatus, newStatus,
                changedBy != null ? changedBy.getEmail() : "SYSTEM");
    }
}
