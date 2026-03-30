package com.loanflow.integration;


import com.loanflow.integration.dto.CreditBureauResponse;

import java.math.BigDecimal;

public interface CreditBureauService {
    CreditBureauServiceImpl.ExternalDebtResult fetchExternalEmi(String panNumber);
}
