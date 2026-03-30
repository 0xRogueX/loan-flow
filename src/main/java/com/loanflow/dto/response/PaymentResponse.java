package com.loanflow.dto.response;

import com.loanflow.enums.PaymentMode;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {

    private UUID id;

    private String receiptNumber; // <=== Added this

    private UUID loanId;

    private Integer installmentNumber;

    private BigDecimal paidAmount;

    private PaymentMode paymentMode;

    private LocalDateTime paidAt;
}