package com.fayemanalo.smartpark.dto;

import com.fayemanalo.smartpark.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record VehicleDto(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "License plate can only contain letters, numbers, and dashes")
        String licensePlate,

        VehicleType type,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Owner name can only contain letters and spaces")
        String ownerName
) {}


