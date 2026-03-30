package com.loanflow.service;

public interface OverdueMonitorService {

    // Daily: marks PENDING EMIs past due date as OVERDUE + applies penalty
    void scanAndMarkOverdue();

    // Daily: transitions DEFAULTED loans (180+ days) to WRITTEN_OFF
    void scanAndMarkWrittenOff();
}
