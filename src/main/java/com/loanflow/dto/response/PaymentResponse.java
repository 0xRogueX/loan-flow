package com.loanflow.dto.response;

import com.loanflow.enums.PaymentMode;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {

    private Long id;

    private String receiptNumber; // <=== Added this

    private Long loanId;

    private Integer installmentNumber;

    private BigDecimal paidAmount;

    private PaymentMode paymentMode;

    private LocalDateTime paidAt;
}