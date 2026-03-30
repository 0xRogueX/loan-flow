package com.loanflow.event;

import com.loanflow.entity.EmiSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event published by the background scheduler to remind a borrower of an upcoming EMI.
 */
@Getter
@AllArgsConstructor
public class PaymentReminderEvent {

    private final EmiSchedule emiSchedule;

}