package com.loanflow.entity.user;

import com.loanflow.entity.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "borrower")
@DiscriminatorValue("BORROWER")
@Getter
@Setter
@NoArgsConstructor
public class Borrower extends User {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String panNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @Column(nullable = false)
    private String occupation;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
}