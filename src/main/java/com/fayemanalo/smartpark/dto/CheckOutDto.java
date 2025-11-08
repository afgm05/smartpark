package com.fayemanalo.smartpark.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckOutDto(
        @NotBlank
        String licensePlate,

        @NotBlank
        String lotId
) {}
