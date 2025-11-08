package com.fayemanalo.smartpark.service;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.dto.ParkingLotStatusDto;
import com.fayemanalo.smartpark.dto.VehiclesInLotDto;
import com.fayemanalo.smartpark.mapper.ParkingLotMapper;
import com.fayemanalo.smartpark.model.ParkingLot;
import com.fayemanalo.smartpark.model.ParkingRecord;
import com.fayemanalo.smartpark.model.Vehicle;
import com.fayemanalo.smartpark.model.VehicleType;
import com.fayemanalo.smartpark.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingLotServiceTest {

    private ParkingLotRepository parkingLotRepository;
    private ParkingLotMapper parkingLotMapper;
    private ParkingLotService parkingLotService;

    @BeforeEach
    void setUp() {
        parkingLotRepository = mock(ParkingLotRepository.class);
        parkingLotMapper = mock(ParkingLotMapper.class);
        parkingLotService = new ParkingLotService(parkingLotRepository, parkingLotMapper);
    }

    // =======================
    // Tests for save()
    // =======================
    @Test
    void save_shouldReturnParkingLotDto_whenParkingLotDoesNotExist() {
        // Given
        ParkingLotDto dto = new ParkingLotDto("LOT1", "Main Street", 10, 0);

        ParkingLot entity = new ParkingLot();
        entity.setLotId("LOT1");
        entity.setLocation("Main Street");
        entity.setCapacity(10);
        entity.setOccupiedSpaces(0);

        when(parkingLotMapper.toEntity(dto)).thenReturn(entity);
        when(parkingLotRepository.existsById("LOT1")).thenReturn(false);
        when(parkingLotRepository.save(entity)).thenReturn(entity);
        when(parkingLotMapper.toDto(entity)).thenReturn(dto);

        // When
        ParkingLotDto result = parkingLotService.save(dto);

        // Then
        assertEquals(dto.lotId(), result.lotId());
        assertEquals(dto.location(), result.location());
        assertEquals(dto.capacity(), result.capacity());
        assertEquals(dto.occupiedSpaces(), result.occupiedSpaces());

        verify(parkingLotRepository).existsById("LOT1");
        verify(parkingLotRepository).save(entity);
        verify(parkingLotMapper).toEntity(dto);
        verify(parkingLotMapper).toDto(entity);
    }

    @Test
    void save_shouldThrowException_whenParkingLotAlreadyExists() {
        // Given
        ParkingLotDto dto = new ParkingLotDto("LOT1", "Main Street", 10, 0);

        ParkingLot entity = new ParkingLot();
        entity.setLotId("LOT1");

        when(parkingLotMapper.toEntity(dto)).thenReturn(entity);
        when(parkingLotRepository.existsById("LOT1")).thenReturn(true);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> parkingLotService.save(dto));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Parking lot with this ID already exists", exception.getReason());

        verify(parkingLotRepository).existsById("LOT1");
        verify(parkingLotRepository, never()).save(any());
        verify(parkingLotMapper, never()).toDto(any());
    }

    // =======================
    // Tests for getStatus()
    // =======================
    @Test
    void getStatus_shouldReturnParkingLotStatusDto_whenLotExists() {
        // Given
        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");
        lot.setLocation("Main Street");
        lot.setCapacity(50);
        lot.setOccupiedSpaces(10);

        // Mock
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));

        // When
        ParkingLotStatusDto status = parkingLotService.getStatus("LOT1");

        // Then
        assertEquals("LOT1", status.lotId());
        assertEquals("Main Street", status.location());
        assertEquals(50, status.capacity());
        assertEquals(10, status.occupiedSpaces());
        assertEquals(40, status.availableSpaces()); // capacity - occupiedSpaces
    }

    @Test
    void getStatus_shouldThrowException_whenLotNotFound() {
        // Mock
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> parkingLotService.getStatus("LOT1"));

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Parking lot not found", exception.getReason());
    }

    // =======================
    // Tests for getVehiclesInLot()
    // =======================
    @Test
    void getVehiclesInLot_shouldReturnVehicles_whenLotHasActiveRecords() {
        // Given
        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("ABC-123");
        vehicle1.setType(VehicleType.CAR);
        vehicle1.setOwnerName("John Doe");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("DEF-456");
        vehicle2.setType(VehicleType.CAR);
        vehicle2.setOwnerName("Emily Brown");

        ParkingRecord activeRecord1 = new ParkingRecord();
        activeRecord1.setVehicle(vehicle1);
        activeRecord1.setCheckInTime(LocalDateTime.now());

        ParkingRecord activeRecord2 = new ParkingRecord();
        activeRecord2.setVehicle(vehicle2);
        activeRecord2.setCheckInTime(LocalDateTime.now());

        List<ParkingRecord> records = new ArrayList<>();
        records.add(activeRecord1);
        records.add(activeRecord2);
        lot.setParkingRecords(records);

        // Mock
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));

        // When
        List<VehiclesInLotDto> vehicles = parkingLotService.getVehiclesInLot("LOT1");

        // Then
        assertEquals(2, vehicles.size());
        assertEquals("ABC-123", vehicles.get(0).licensePlate());
        assertEquals("DEF-456", vehicles.get(1).licensePlate());
    }

    @Test
    void getVehiclesInLot_shouldThrowException_whenLotNotFound() {
        // Mock
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> parkingLotService.getVehiclesInLot("LOT1"));

        // Then
        assertEquals("Parking lot not found", exception.getReason());
    }

}