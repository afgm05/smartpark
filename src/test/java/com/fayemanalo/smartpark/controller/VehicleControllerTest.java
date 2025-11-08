package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.model.VehicleType;
import com.fayemanalo.smartpark.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    private VehicleService vehicleService;
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        vehicleService = mock(VehicleService.class);
        vehicleController = new VehicleController(vehicleService);
    }

    @Test
    void registerVehicle_shouldReturnCreatedResponse_whenVehicleIsSaved() {
        // Given
        VehicleDto vehicleDto = VehicleDto.builder()
                .licensePlate("XYZ-987")
                .type(VehicleType.TRUCK)
                .ownerName("Alice Smith")
                .build();

        when(vehicleService.save(vehicleDto)).thenReturn(vehicleDto);

        // When
        ResponseEntity<VehicleDto> response = vehicleController.registerVehicle(vehicleDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(vehicleDto, response.getBody());
        verify(vehicleService, times(1)).save(vehicleDto);
    }

    @Test
    void registerVehicle_shouldPropagateServiceException() {
        // Given
        VehicleDto requestDto = VehicleDto.builder()
                .licensePlate("INVALID")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(vehicleService.save(requestDto))
                .thenThrow(new IllegalArgumentException("Invalid vehicle data"));

        // When
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleController.registerVehicle(requestDto)
        );

        // Then
        assertEquals("Invalid vehicle data", thrown.getMessage());
        verify(vehicleService, times(1)).save(requestDto);
    }

}