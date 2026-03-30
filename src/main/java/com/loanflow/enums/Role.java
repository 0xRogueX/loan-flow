package com.loanflow.enums;

public enum Role {
    ADMIN,
    BORROWER,
    LOAN_OFFICER,
    /** Used only in audit logs for actions performed by the system scheduler. Never assigned to a real user. */
    SYSTEM
}
