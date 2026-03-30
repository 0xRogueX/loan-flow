package com.loanflow.service;

import com.loanflow.dto.request.LoginRequest;
import com.loanflow.dto.request.RegisterRequest;
import com.loanflow.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}
