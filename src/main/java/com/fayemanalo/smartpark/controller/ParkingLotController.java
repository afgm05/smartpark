package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.dto.ParkingLotStatusDto;
import com.fayemanalo.smartpark.dto.VehiclesInLotDto;
import com.fayemanalo.smartpark.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing parking lots.
 *
 * Provides endpoints to register a parking lot, check its status,
 * and list vehicles currently parked in a lot.
 */
@RestController
@RequestMapping("/api/parking")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    // Register a new parking lot
    @PostMapping
    public ResponseEntity<ParkingLotDto> registerParkingLot(@Valid @RequestBody ParkingLotDto parkingLotDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingLotService.save(parkingLotDto));
    }

    // Get the status of a parking lot
    @GetMapping("/{lotId}/status")
    public ResponseEntity<ParkingLotStatusDto> getStatus(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingLotService.getStatus(lotId));
    }

    // Get a list of vehicles currently parked in a specific lot.
    @GetMapping("/{lotId}/vehicles")
    public ResponseEntity<List<VehiclesInLotDto>> getVehiclesInLot(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingLotService.getVehiclesInLot(lotId));
    }

}
