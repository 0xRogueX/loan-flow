package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.Borrower;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String flatNo;

    @NotBlank
    @Column(nullable = false)
    private String area;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @NotBlank
    @Pattern(regexp = "^[1-9][0-9]{5}$") // Indian pincode
    @Column(nullable = false)
    private String pincode;

    //bidirectional mapping — LAZY to avoid eager-loading the full Borrower graph
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private Borrower borrower;
}