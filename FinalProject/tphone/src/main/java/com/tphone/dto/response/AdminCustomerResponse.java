package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdminCustomerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String avatarUrl;
    private String provider;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;
    private Long totalOrders;
    private BigDecimal totalSpent;
}