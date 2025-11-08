package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.dto.ParkingLotStatusDto;
import com.fayemanalo.smartpark.dto.VehiclesInLotDto;
import com.fayemanalo.smartpark.model.VehicleType;
import com.fayemanalo.smartpark.service.ParkingLotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingLotControllerTest {

    private ParkingLotService parkingLotService;
    private ParkingLotController parkingLotController;

    @BeforeEach
    void setUp() {
        parkingLotService = mock(ParkingLotService.class);
        parkingLotController = new ParkingLotController(parkingLotService);
    }

    // ===============================
    // Tests for registerParkingLot()
    // ===============================
    @Test
    void registerParkingLot_shouldReturnCreatedResponse_whenSaveSucceeds() {
        // Given
        ParkingLotDto dto = ParkingLotDto.builder()
                .lotId("LOT1")
                .location("Main Street")
                .capacity(10)
                .occupiedSpaces(0)
                .build();

        when(parkingLotService.save(dto)).thenReturn(dto);

        // When
        ResponseEntity<ParkingLotDto> response = parkingLotController.registerParkingLot(dto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(parkingLotService, times(1)).save(dto);
    }

    @Test
    void registerParkingLot_shouldPropagateException_whenServiceThrows() {
        ParkingLotDto dto = ParkingLotDto.builder()
                .lotId("LOT1")
                .location("Main Street")
                .capacity(10)
                .occupiedSpaces(0)
                .build();

        when(parkingLotService.save(dto)).thenThrow(new IllegalArgumentException("Duplicate lot"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> parkingLotController.registerParkingLot(dto)
        );

        assertEquals("Duplicate lot", thrown.getMessage());
        verify(parkingLotService, times(1)).save(dto);
    }

    // ========================
    // Tests for getStatus()
    // ========================
    @Test
    void getStatus_shouldReturnOkResponse_whenLotExists() {
        // Given
        String lotId = "LOT1";
        ParkingLotStatusDto statusDto = new ParkingLotStatusDto(lotId, "Main Street", 10, 2, 8);

        when(parkingLotService.getStatus(lotId)).thenReturn(statusDto);

        // When
        ResponseEntity<ParkingLotStatusDto> response = parkingLotController.getStatus(lotId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statusDto, response.getBody());
        verify(parkingLotService, times(1)).getStatus(lotId);
    }

    @Test
    void getStatus_shouldPropagateException_whenServiceThrows() {
        String lotId = "LOT1";

        when(parkingLotService.getStatus(lotId)).thenThrow(new IllegalArgumentException("Lot not found"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> parkingLotController.getStatus(lotId)
        );

        assertEquals("Lot not found", thrown.getMessage());
        verify(parkingLotService, times(1)).getStatus(lotId);
    }

    // ========================
    // Tests for getVehiclesInLot()
    // ========================
    @Test
    void getVehiclesInLot_shouldReturnOkResponse_whenVehiclesExist() {
        // Given
        String lotId = "LOT1";
        VehiclesInLotDto vehicleDto = new VehiclesInLotDto("ABC-123", VehicleType.CAR, "John Doe", LocalDateTime.now());
        List<VehiclesInLotDto> vehicles = List.of(vehicleDto);

        when(parkingLotService.getVehiclesInLot(lotId)).thenReturn(vehicles);

        // When
        ResponseEntity<List<VehiclesInLotDto>> response = parkingLotController.getVehiclesInLot(lotId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehicles, response.getBody());
        verify(parkingLotService, times(1)).getVehiclesInLot(lotId);
    }

    @Test
    void getVehiclesInLot_shouldPropagateException_whenServiceThrows() {
        String lotId = "LOT1";

        when(parkingLotService.getVehiclesInLot(lotId)).thenThrow(new IllegalArgumentException("Lot not found"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> parkingLotController.getVehiclesInLot(lotId)
        );

        assertEquals("Lot not found", thrown.getMessage());
        verify(parkingLotService, times(1)).getVehiclesInLot(lotId);
    }

}