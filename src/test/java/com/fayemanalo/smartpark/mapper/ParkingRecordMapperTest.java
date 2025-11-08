package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.ParkingRecordDto;
import com.fayemanalo.smartpark.model.ParkingLot;
import com.fayemanalo.smartpark.model.ParkingRecord;
import com.fayemanalo.smartpark.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParkingRecordMapperTest {

    private ParkingRecordMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ParkingRecordMapper();
    }

    @Test
    void toDto_shouldReturnDto_whenRecordIsNotNull() {
        // Given
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");

        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusHours(2);

        ParkingRecord record = new ParkingRecord();
        record.setVehicle(vehicle);
        record.setParkingLot(lot);
        record.setCheckInTime(checkIn);
        record.setCheckOutTime(checkOut);

        // When
        ParkingRecordDto dto = mapper.toDto(record);

        // Then
        assertNotNull(dto);
        assertEquals("ABC-123", dto.licensePlate());
        assertEquals("LOT1", dto.lotId());
        assertEquals(checkIn, dto.checkInTime());
        assertEquals(checkOut, dto.checkOutTime());
    }

    @Test
    void toDto_shouldReturnNull_whenRecordIsNull() {
        // When
        ParkingRecordDto dto = mapper.toDto(null);

        // Then
        assertNull(dto);
    }

}