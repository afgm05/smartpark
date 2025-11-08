package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.CheckInDto;
import com.fayemanalo.smartpark.dto.CheckOutDto;
import com.fayemanalo.smartpark.dto.ParkingRecordDto;
import com.fayemanalo.smartpark.service.ParkingRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing parking records.
 *
 * Provides endpoints to check-in and check-out vehicles in parking lots.
 */
@RestController
@RequestMapping("/api/parkingrecords")
public class ParkingRecordController {

    private final ParkingRecordService parkingRecordService;

    public ParkingRecordController(ParkingRecordService parkingRecordService) {
        this.parkingRecordService = parkingRecordService;
    }

    // Check-in a vehicle
    @PostMapping("/checkin")
    public ResponseEntity<ParkingRecordDto> checkIn(@Valid @RequestBody CheckInDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingRecordService.checkIn(dto));
    }

    // Check-out a vehicle
    @PostMapping("/checkout")
    public ResponseEntity<ParkingRecordDto> checkOut(@Valid @RequestBody CheckOutDto dto) {
        return ResponseEntity.ok(parkingRecordService.checkOut(dto));
    }
}
