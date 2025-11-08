package com.fayemanalo.smartpark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CheckInDto(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "License plate can only contain letters, numbers, and dashes")
        String licensePlate,

        @NotBlank
        @Size(max = 50)
        String lotId
) {}
