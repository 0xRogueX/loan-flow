package com.loanflow.service;

import com.loanflow.entity.Payment;

public interface PdfGeneratorService {
    byte[] generatePaymentReceipt(Payment payment);
}
