package com.loanflow.dto.request;


import com.loanflow.entity.Address;
import com.loanflow.enums.AdminAccessLevel;
import com.loanflow.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    @NotNull(message = "Role is required")
    private Role role;

    // ── Borrower-specific (required when role = BORROWER) ──
    private BigDecimal monthlyIncome;

    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN format")
    private String panNumber;

    private String occupation;

    private LocalDate dateOfBirth;

    private Address address;

    // ── Officer-specific (required when role = LOAN_OFFICER) ──
    private String designation;

    @PositiveOrZero
    private Integer loansApprovedCount;

    @PositiveOrZero
    private Integer loansRejectedCount;
//    private BigDecimal maxApprovalLimit;

    // ── Admin-specific (required when role = ADMIN) ──
    private AdminAccessLevel accessLevel;

}



