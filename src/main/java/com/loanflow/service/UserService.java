package com.loanflow.service;

import com.loanflow.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    void deactivateUser(UUID userId);
}
