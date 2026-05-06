package com.tphone.controller.auth;

import com.tphone.dto.request.LoginRequest;
import com.tphone.dto.request.RefreshTokenRequest;
import com.tphone.dto.request.RegisterRequest;
import com.tphone.dto.request.UpdateProfileRequest;
import com.tphone.dto.response.ApiResponse;
import com.tphone.dto.response.AuthResponse;
import com.tphone.dto.response.CurrentUserResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.AccountService;
import com.tphone.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ApiResponse<>(true, "Register successfully", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ApiResponse<>(true, "Login successfully", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return new ApiResponse<>(true, "Refresh token successfully", authService.refreshToken(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ApiResponse<>(
                true,
                "Current user",
                accountService.getCurrentUser(userDetails.getAccountId())
        );
    }

    @PutMapping("/me")
    public ApiResponse<CurrentUserResponse> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return new ApiResponse<>(
                true,
                "Update profile successfully",
                accountService.updateCurrentUser(userDetails.getAccountId(), request)
        );
    }
}