package com.loanflow.controller;

import com.loanflow.dto.response.ApiResponse;
import com.loanflow.dto.response.AuditLogResponse;
import com.loanflow.dto.response.UserResponse;
import com.loanflow.service.AuditService;
import com.loanflow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints in this controller
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        log.info("Admin requested to fetch all users.");

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(ApiResponse.ok("Users fetched successfully.", users));
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable UUID userId) {

        log.info("Admin requested to deactivate user with ID: {}", userId);

        userService.deactivateUser(userId);

        return ResponseEntity.ok(ApiResponse.ok("User deactivated successfully.", null));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("Admin requested to fetch audit logs - Page: {}, Size: {}", page, size);

        // 1. Determine sort direction dynamically
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 2. Create the Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // 3. Fetch the paginated data
        Page<AuditLogResponse> paginatedLogs = auditService.getAllAuditLogs(pageable);

        return ResponseEntity.ok(ApiResponse.ok("Audit logs fetched successfully.", paginatedLogs));
    }
}