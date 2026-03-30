package com.loanflow.repository;

import com.loanflow.entity.user.LoanOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanOfficerRepository extends JpaRepository<LoanOfficer, UUID> {

    Optional<LoanOfficer> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}
