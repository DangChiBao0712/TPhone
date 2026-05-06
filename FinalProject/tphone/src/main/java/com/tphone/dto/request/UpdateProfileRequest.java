package com.tphone.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 120, message = "Full name must be <= 120 characters")
    private String fullName;

    @Pattern(
            regexp = "^$|^[0-9+\\-\\s]{8,20}$",
            message = "Phone number is invalid"
    )
    private String phone;

    private LocalDate birthDate;

    @Size(max = 255, message = "Avatar url must be <= 255 characters")
    private String avatarUrl;
}