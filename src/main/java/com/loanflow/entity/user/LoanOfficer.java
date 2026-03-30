package com.loanflow.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "loan_officer_profiles")
@DiscriminatorValue("LOAN_OFFICER")
@Getter
@Setter
@NoArgsConstructor
public class LoanOfficer extends User{

//    @Id
//    private UUID id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name = "user_id")
//    private User user;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String employeeId;

    @NotBlank
    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private Integer loansApprovedCount = 0;

    @Column(nullable = false)
    private Integer loansRejectedCount = 0;
}