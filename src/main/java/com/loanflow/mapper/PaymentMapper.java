package com.loanflow.mapper;

import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.entity.Payment;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * loan.id             → loanId            (context for the borrower)
     * emiSchedule.installmentNumber → installmentNumber  (human-readable label)
     * Both source fields are guaranteed non-null on a valid Payment entity.
     */
    @Mapping(target = "loanId",             source = "loan.id")
    @Mapping(target = "installmentNumber",   source = "emiSchedule.installmentNumber")
    PaymentResponse toResponse(Payment payment);

    /** Used by PaymentController.getPaymentsByLoan() */
    List<PaymentResponse> toResponseList(List<Payment> payments);
}

