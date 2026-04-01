package com.loanflow.repository;

import com.loanflow.entity.user.LoanOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanOfficerRepository extends JpaRepository<LoanOfficer, Long> {

    @Query(value = "SELECT nextval('employee_id_seq')", nativeQuery = true)
    Long getNextEmployeeIdSequence();

    Optional<LoanOfficer> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}
