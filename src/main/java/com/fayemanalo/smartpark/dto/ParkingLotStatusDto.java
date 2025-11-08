package com.fayemanalo.smartpark.dto;

public record ParkingLotStatusDto(
        String lotId,
        String location,
        int capacity,
        int occupiedSpaces,
        int availableSpaces
) {}
