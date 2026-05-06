package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private String avatarUrl;
    private String status;
    private Boolean enabled;
    private LocalDateTime lastLoginAt;
    private List<String> roles;
}