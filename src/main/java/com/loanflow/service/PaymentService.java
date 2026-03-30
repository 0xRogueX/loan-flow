package com.loanflow.service;

import com.loanflow.dto.request.PaymentSimulationRequest;
import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.entity.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    @Transactional
    PaymentResponse simulatePayment(PaymentSimulationRequest request, User borrower);

    List<PaymentResponse> getPaymentsByLoanNumber(String loanNumber);
}
