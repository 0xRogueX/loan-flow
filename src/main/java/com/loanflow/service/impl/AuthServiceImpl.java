package com.loanflow.service.impl;

import com.loanflow.dto.request.LoginRequest;
import com.loanflow.dto.request.RegisterRequest;
import com.loanflow.dto.response.AuthResponse;
import com.loanflow.entity.user.Admin;
import com.loanflow.entity.user.Borrower;
import com.loanflow.entity.user.LoanOfficer;
import com.loanflow.entity.user.User;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.repository.UserRepository;
import com.loanflow.security.JwtTokenProvider;
import com.loanflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        log.debug("Processing registration for email: {}", req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessRuleException(
                    "Email already registered: " + req.getEmail());
        }

        // Create correct subtype — JPA routes to the right table
        User user = switch (req.getRole()) {
            case BORROWER -> {
                Borrower b = new Borrower();
                b.setMonthlyIncome(req.getMonthlyIncome());
                b.setPanNumber(req.getPanNumber());
                b.setOccupation(req.getOccupation());
                yield b;
            }
            case LOAN_OFFICER -> {
                LoanOfficer o = new LoanOfficer();
                o.setEmployeeId(req.getEmployeeId());
                o.setDesignation(req.getDesignation());
//                o.setMaxApprovalLimit(req.getMaxApprovalLimit());
                yield o;
            }
            case ADMIN -> {
                Admin a = new Admin();
                a.setAccessLevel(req.getAccessLevel());
                yield a;
            }
        };

        // set common fields
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());

        user.setActive(true);
        user.setDeleted(false);

        User saved = userRepository.save(user);

        log.info("Successfully registered new user with email: {} and role: {}", saved.getEmail(), saved.getRole());

        return AuthResponse.builder()
                .role(saved.getRole())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest req) {

        log.debug("Processing login for email: {}", req.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.getEmail()));

        String token = jwtTokenProvider.generateToken(auth);

        log.info("Successfully logged in user: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
