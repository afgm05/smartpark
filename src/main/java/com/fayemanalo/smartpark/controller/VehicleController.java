package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing vehicles in the SmartPark system.
 *
 * Provides endpoints for registering new vehicles.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Register a vehicle
    @PostMapping
    public ResponseEntity<VehicleDto> registerVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.save(vehicleDto));
    }

}
