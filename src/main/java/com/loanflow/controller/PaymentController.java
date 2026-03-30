package com.loanflow.controller;


import com.loanflow.dto.request.PaymentSimulationRequest;
import com.loanflow.dto.response.ApiResponse;
import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.entity.Payment;
import com.loanflow.entity.user.User;
import com.loanflow.enums.Role;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.repository.PaymentRepository;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.PaymentService;
import com.loanflow.service.PdfGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final SecurityUtils securityUtils;

    @PostMapping("/simulate")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulate(
            @Valid @RequestBody PaymentSimulationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                paymentService.simulatePayment(
                        request, securityUtils.getCurrentUser())));
    }

    @GetMapping("/loan/{loanNumber}")
    @PreAuthorize("hasAnyRole('BORROWER','LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByLoan(@PathVariable String loanNumber) {

        List<PaymentResponse> payments = paymentService.getPaymentsByLoanNumber(loanNumber);
        return ResponseEntity.ok(ApiResponse.ok(
                "Payments retrieved successfully", payments));
    }


    @GetMapping("/receipt/{receiptNumber}/download")
    @PreAuthorize("hasAnyRole('BORROWER', 'ADMIN')")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String receiptNumber) {

        // 1. Fetch the payment
        Payment payment = paymentRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found."));

        // 2. Security Check: Only the owner (or an Admin) can download this receipt
        User currentUser = securityUtils.getCurrentUser();
        if (!payment.getBorrower().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedAccessException("You are not authorized to download this receipt.");
        }

        // 3. Generate the PDF byte array
        byte[] pdfBytes = pdfGeneratorService.generatePaymentReceipt(payment);

        // 4. Set headers so the browser triggers a file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Receipt_" + receiptNumber + ".pdf");
        headers.setContentLength(pdfBytes.length);

        // 5. Return the file!
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
