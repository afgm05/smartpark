package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.ParkingRecordDto;
import com.fayemanalo.smartpark.model.ParkingRecord;
import org.springframework.stereotype.Component;

@Component
public class ParkingRecordMapper {

    // Convert Entity to DTO
    public ParkingRecordDto toDto(ParkingRecord record) {
        if (record == null) return null;

        return new ParkingRecordDto(
                record.getVehicle().getLicensePlate(),
                record.getParkingLot().getLotId(),
                record.getCheckInTime(),
                record.getCheckOutTime()
        );
    }
}
