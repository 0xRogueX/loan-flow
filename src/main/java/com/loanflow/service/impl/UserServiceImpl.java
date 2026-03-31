package com.loanflow.service.impl;

import com.loanflow.dto.response.UserResponse;
import com.loanflow.entity.user.User;
import com.loanflow.enums.EntityType;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.mapper.UserMapper;
import com.loanflow.repository.UserRepository;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.AuditService;
import com.loanflow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users from the database.");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateUser(Long targetUserId) {

        User currentAdmin = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + targetUserId));

        // Prevent self-deactivation
        if (targetUser.getId().equals(currentAdmin.getId())) {
            throw new BusinessRuleException("You cannot deactivate your own account.");
        }

        if (!targetUser.isActive()) {
            throw new BusinessRuleException("This user is already deactivated.");
        }
        targetUser.setActive(false);
        userRepository.save(targetUser);
        log.info("User {} was successfully deactivated", targetUser.getEmail());
    }
}