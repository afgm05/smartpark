package com.fayemanalo.smartpark.service;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.mapper.VehicleMapper;
import com.fayemanalo.smartpark.model.Vehicle;
import com.fayemanalo.smartpark.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    /**
     * Saves a new vehicle.
     *
     * @param vehicleDto the vehicle data transfer object
     * @return the saved vehicle as DTO
     * @throws ResponseStatusException if a vehicle with the same license plate already exists
     */
    public VehicleDto save(VehicleDto vehicleDto) {

        var vehicle = vehicleMapper.toEntity(vehicleDto);

        // Check if vehicle already exists
        if (vehicleRepository.existsById(vehicle.getLicensePlate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle with this license plate already exists"
            );
        }

        var savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(savedVehicle);
    }
}
