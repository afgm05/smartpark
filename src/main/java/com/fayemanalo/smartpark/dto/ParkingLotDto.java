package com.fayemanalo.smartpark.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ParkingLotDto (
    @NotBlank
    @Size(max = 50)
    String lotId,

    @NotBlank
    String location,

    @Min(1)
    int capacity,

    @Min(0)
    int occupiedSpaces
) {}