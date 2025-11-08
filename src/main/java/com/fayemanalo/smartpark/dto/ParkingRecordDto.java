package com.fayemanalo.smartpark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ParkingRecordDto(
        String licensePlate,

        String lotId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a E")
        LocalDateTime checkInTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a E")
        LocalDateTime checkOutTime
) {}
