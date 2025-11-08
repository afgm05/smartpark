package com.fayemanalo.smartpark.controller;

import com.fayemanalo.smartpark.dto.CheckInDto;
import com.fayemanalo.smartpark.dto.CheckOutDto;
import com.fayemanalo.smartpark.dto.ParkingRecordDto;
import com.fayemanalo.smartpark.service.ParkingRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingRecordControllerTest {

    private ParkingRecordService parkingRecordService;
    private ParkingRecordController parkingRecordController;

    @BeforeEach
    void setUp() {
        parkingRecordService = mock(ParkingRecordService.class);
        parkingRecordController = new ParkingRecordController(parkingRecordService);
    }

    // =======================
    // Tests for checkIn()
    // =======================
    @Test
    void checkIn_shouldReturnCreatedResponse_whenCheckInSucceeds() {
        // Given
        CheckInDto checkInDto = new CheckInDto("ABC-123", "LOT1");
        ParkingRecordDto recordDto = new ParkingRecordDto("ABC-123", "LOT1", LocalDateTime.now(), null);

        // Mock
        when(parkingRecordService.checkIn(checkInDto)).thenReturn(recordDto);

        // When
        ResponseEntity<ParkingRecordDto> response = parkingRecordController.checkIn(checkInDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(recordDto, response.getBody());
        verify(parkingRecordService, times(1)).checkIn(checkInDto);
    }

    @Test
    void checkIn_shouldPropagateServiceException() {
        CheckInDto checkInDto = new CheckInDto("INVALID", "LOT1");

        when(parkingRecordService.checkIn(checkInDto))
                .thenThrow(new IllegalArgumentException("Invalid check-in"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> parkingRecordController.checkIn(checkInDto)
        );

        assertEquals("Invalid check-in", thrown.getMessage());
        verify(parkingRecordService, times(1)).checkIn(checkInDto);
    }


    // =======================
    // Tests for checkOut()
    // =======================
    @Test
    void checkOut_shouldReturnOkResponse_whenCheckOutSucceeds() {
        // Given
        CheckOutDto checkOutDto = new CheckOutDto("ABC-123", "LOT1");
        ParkingRecordDto recordDto = new ParkingRecordDto("ABC-123", "LOT1", LocalDateTime.now().minusHours(2), LocalDateTime.now());

        when(parkingRecordService.checkOut(checkOutDto)).thenReturn(recordDto);

        // When
        ResponseEntity<ParkingRecordDto> response = parkingRecordController.checkOut(checkOutDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recordDto, response.getBody());
        verify(parkingRecordService, times(1)).checkOut(checkOutDto);
    }



    @Test
    void checkOut_shouldPropagateServiceException() {
        CheckOutDto checkOutDto = new CheckOutDto("INVALID", "LOT1");

        when(parkingRecordService.checkOut(checkOutDto))
                .thenThrow(new IllegalArgumentException("Invalid check-out"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> parkingRecordController.checkOut(checkOutDto)
        );

        assertEquals("Invalid check-out", thrown.getMessage());
        verify(parkingRecordService, times(1)).checkOut(checkOutDto);
    }

}