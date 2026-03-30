package com.loanflow.strategy;

import com.loanflow.enums.LoanStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanStrategyFactory {

    private final FlatRateStrategy flatRateStrategy;
    private final ReducingBalanceStrategy reducingBalanceStrategy;
    private final StepUpEmiStrategy stepUpEmiStrategy;

    public EmiCalculationStrategy resolve(LoanStrategy strategy) {
        return switch (strategy) {
            case FLAT_RATE_LOAN -> flatRateStrategy;
            case REDUCING_BALANCE_LOAN -> reducingBalanceStrategy;
            case STEP_UP_EMI_LOAN -> stepUpEmiStrategy;
        };
    }
}
