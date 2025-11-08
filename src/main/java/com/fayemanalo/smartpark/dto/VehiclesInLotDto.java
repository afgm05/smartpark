package com.fayemanalo.smartpark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fayemanalo.smartpark.model.VehicleType;

import java.time.LocalDateTime;

public record VehiclesInLotDto(
        String licensePlate,
        VehicleType type,
        String ownerName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a E")
        LocalDateTime checkInTime
) {}
