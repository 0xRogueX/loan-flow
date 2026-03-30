package com.loanflow.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditBureauResponse {

    private String panNumber;
    private BigDecimal totalMonthlyEmi;
    private boolean found;

    public static CreditBureauResponse unavailable(String panNumber) {
        return CreditBureauResponse.builder()
                .panNumber(panNumber)
                .totalMonthlyEmi(BigDecimal.ZERO)   // Assuming zero EMI bcz not connect to credit bureau
                .found(false)
                .build();
    }
}
    