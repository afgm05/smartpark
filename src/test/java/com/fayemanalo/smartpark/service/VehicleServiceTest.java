package com.fayemanalo.smartpark.service;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.mapper.VehicleMapper;
import com.fayemanalo.smartpark.model.Vehicle;
import com.fayemanalo.smartpark.model.VehicleType;
import com.fayemanalo.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    private VehicleRepository vehicleRepository;
    private VehicleMapper vehicleMapper;
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        vehicleMapper = mock(VehicleMapper.class);
        vehicleService = new VehicleService(vehicleRepository, vehicleMapper);
    }

    @Test
    void save_shouldReturnVehicleDto_whenVehicleDoesNotExist() {
        // Given
        VehicleDto vehicleDto = new VehicleDto("ABC-123", VehicleType.CAR, "John Doe");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");
        vehicle.setType(VehicleType.CAR);
        vehicle.setOwnerName("John Doe");

        // Mock mapper and repository behavior
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(vehicleRepository.existsById("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        // When
        VehicleDto result = vehicleService.save(vehicleDto);

        // Then
        assertEquals(vehicleDto.licensePlate(), result.licensePlate());
        assertEquals(vehicleDto.type(), result.type());
        assertEquals(vehicleDto.ownerName(), result.ownerName());

        verify(vehicleRepository).existsById("ABC-123");
        verify(vehicleRepository).save(vehicle);
        verify(vehicleMapper).toEntity(vehicleDto);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void save_shouldThrowException_whenVehicleAlreadyExists() {
        // Given
        VehicleDto vehicleDto = new VehicleDto("ABC-123", VehicleType.CAR, "John Doe");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");
        vehicle.setType(VehicleType.CAR);
        vehicle.setOwnerName("John Doe");

        // Mock
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(vehicleRepository.existsById("ABC-123")).thenReturn(true);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> vehicleService.save(vehicleDto));
        // Then
        assertEquals("Vehicle with this license plate already exists", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        // Verify
        verify(vehicleMapper).toEntity(vehicleDto);
        verify(vehicleRepository).existsById("ABC-123");
        verify(vehicleRepository, never()).save(any());
        verify(vehicleMapper, never()).toDto(any());
    }

}