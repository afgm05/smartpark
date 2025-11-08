package com.fayemanalo.smartpark.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VehicleType {
    CAR,
    MOTORCYCLE,
    TRUCK;

    @JsonCreator
    public static VehicleType from(String value) {
        if (value == null) return null;
        return VehicleType.valueOf(value.toUpperCase());
    }
}
