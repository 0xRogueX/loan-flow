package com.loanflow.integration;

import com.loanflow.constants.LoanConstants;
import com.loanflow.exception.CreditBureauException;
import com.loanflow.integration.dto.CreditBureauResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditBureauServiceImpl implements CreditBureauService{

    private final CreditBureauClient client;

    @Override
    public ExternalDebtResult fetchExternalEmi(String panNumber) {
        try {
            // Attempt to fetch real-time data from the external Bureau API
            CreditBureauResponse response = client.fetchReport(panNumber);

            BigDecimal emi = (response != null && response.getTotalMonthlyEmi() != null)
                    ? response.getTotalMonthlyEmi()
                    : BigDecimal.ZERO;

            return new ExternalDebtResult(emi, LoanConstants.BUREAU_STATUS_AVAILABLE);

        } catch (CreditBureauException e) {
            log.warn("Credit Bureau unavailable or timed out for PAN: {}. Triggering fallback response.", panNumber);

            // Generate the fallback response using your static builder method
            CreditBureauResponse fallbackResponse = CreditBureauResponse.unavailable(panNumber);

            // Return the result mapping the fallback values
            return new ExternalDebtResult(
                    fallbackResponse.getTotalMonthlyEmi(),
                    LoanConstants.BUREAU_STATUS_UNAVAILABLE
            );
        }
    }

    public record ExternalDebtResult(
            BigDecimal externalMonthlyEmi,
            String bureauStatus
    ) {}
}
