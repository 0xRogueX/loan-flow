package com.loanflow.service.impl;

import com.loanflow.dto.response.UserResponse;
import com.loanflow.entity.user.User;
import com.loanflow.enums.EntityType;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.mapper.UserMapper;
import com.loanflow.repository.UserRepository;
import com.loanflow.service.AuditService;
import com.loanflow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final SecurityUtils securityUtils;
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
        public void deactivateUser(UUID targetUserId) {
//            // 1. Identify the Admin performing the action
//            User currentAdmin = securityUtils.getCurrentUser();
//
//            // 2. Fetch the user to be deactivated
//            User targetUser = userRepository.findById(targetUserId)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + targetUserId));
//
//            // 3. Business Rule: Prevent self-deactivation (Admin locking themselves out)
//            if (targetUser.getId().equals(currentAdmin.getId())) {
//                log.warn("Admin {} attempted to deactivate their own account.", currentAdmin.getEmail());
//                throw new BusinessRuleException("You cannot deactivate your own admin account.");
//            }
//
//            // 4. Business Rule: Check if already deactivated
//            // (Assuming your User entity has an isActive() or isEnabled() boolean flag for Spring Security)
//            if (!targetUser.isActive()) {
//                throw new BusinessRuleException("This user is already deactivated.");
//            }
//
//            // 5. Deactivate and save
//            targetUser.setActive(false); // Update this to targetUser.setEnabled(false) if that's what your entity uses
//            userRepository.save(targetUser);
//
//            // 6. Record the action in the Audit Log
//            auditService.log(
//                    EntityType.ADMIN_PROFILE,
//                    targetUser.getId(),
//                    "DEACTIVATED",
//                    "ACTIVE",
//                    "INACTIVE",
//                    currentAdmin,
//                    currentAdmin.getRole(), // e.g., "ADMIN"
//                    "Admin manually deactivated the user account."
//            );
//
//            log.info("User {} was successfully deactivated by Admin {}", targetUser.getEmail(), currentAdmin.getEmail());
        }

}
