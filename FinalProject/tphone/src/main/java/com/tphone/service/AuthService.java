package com.tphone.service;

import com.tphone.dto.request.LoginRequest;
import com.tphone.dto.request.RefreshTokenRequest;
import com.tphone.dto.request.RegisterRequest;
import com.tphone.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}