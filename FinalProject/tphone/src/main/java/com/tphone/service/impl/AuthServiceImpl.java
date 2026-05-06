package com.tphone.service.impl;

import com.tphone.dto.request.LoginRequest;
import com.tphone.dto.request.RefreshTokenRequest;
import com.tphone.dto.request.RegisterRequest;
import com.tphone.dto.response.AuthResponse;
import com.tphone.entity.Account;
import com.tphone.entity.RefreshToken;
import com.tphone.entity.Role;
import com.tphone.enums.AccountProvider;
import com.tphone.enums.AccountStatus;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.RefreshTokenRepository;
import com.tphone.repository.RoleRepository;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.security.jwt.JwtService;
import com.tphone.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && accountRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        Role userRole = roleRepository.findByCode("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        Account account = new Account();
        account.setFullName(request.getFullName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setProvider(AccountProvider.LOCAL);
        account.setStatus(AccountStatus.ACTIVE);

        account.setRoles(new HashSet<>());
        account.getRoles().add(userRole);

        Account savedAccount = accountRepository.save(account);

        CustomUserDetails userDetails = new CustomUserDetails(savedAccount);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(savedAccount);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                savedAccount.getId(),
                savedAccount.getEmail()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Account account = userDetails.getAccount();

        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(account);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                account.getId(),
                account.getEmail()
        );
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getRevokedAt() != null) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        Account account = storedToken.getAccount();
        CustomUserDetails userDetails = new CustomUserDetails(account);

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return new AuthResponse(
                newAccessToken,
                storedToken.getToken(),
                "Bearer",
                account.getId(),
                account.getEmail()
        );
    }

    private String createRefreshToken(Account account) {
        String token = java.util.UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));

        refreshTokenRepository.save(refreshToken);

        return token;
    }
}