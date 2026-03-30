package com.loanflow.event;

import com.loanflow.entity.LoanApplication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoanApplicationSubmittedEvent {

    private final LoanApplication application;
}
