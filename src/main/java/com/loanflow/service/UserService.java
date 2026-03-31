package com.loanflow.service;

import com.loanflow.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    void deactivateUser(Long userId);
}
