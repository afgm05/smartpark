package com.fayemanalo.smartpark.service;

import com.fayemanalo.smartpark.dto.CheckInDto;
import com.fayemanalo.smartpark.dto.CheckOutDto;
import com.fayemanalo.smartpark.dto.ParkingRecordDto;
import com.fayemanalo.smartpark.mapper.ParkingRecordMapper;
import com.fayemanalo.smartpark.model.ParkingLot;
import com.fayemanalo.smartpark.model.ParkingRecord;
import com.fayemanalo.smartpark.model.Vehicle;
import com.fayemanalo.smartpark.repository.ParkingLotRepository;
import com.fayemanalo.smartpark.repository.ParkingRecordRepository;
import com.fayemanalo.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParkingRecordServiceTest {

    private ParkingRecordRepository parkingRecordRepository;
    private VehicleRepository vehicleRepository;
    private ParkingLotRepository parkingLotRepository;
    private ParkingRecordMapper parkingRecordMapper;

    private ParkingRecordService service;

    @BeforeEach
    void setUp() {
        // mock dependencies of the class under test
        parkingRecordRepository = mock(ParkingRecordRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        parkingLotRepository = mock(ParkingLotRepository.class);
        parkingRecordMapper = mock(ParkingRecordMapper.class);

        service = new ParkingRecordService(
                parkingRecordRepository,
                vehicleRepository,
                parkingLotRepository,
                parkingRecordMapper
        );
    }

    // =======================
    // Tests for checkIn()
    // =======================
    @Test
    void checkIn_shouldSucceed() {
        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");
        lot.setCapacity(2);
        lot.setOccupiedSpaces(0);

        ParkingRecord savedRecord = ParkingRecord.builder()
                .vehicle(vehicle)
                .parkingLot(lot)
                .checkInTime(LocalDateTime.now())
                .build();

        // Mock repository lookups and mapper
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));
        when(parkingRecordRepository.findByVehicleAndCheckOutTimeIsNull(vehicle)).thenReturn(Optional.empty());

        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(savedRecord);
        when(parkingRecordMapper.toDto(savedRecord)).thenReturn(new ParkingRecordDto(
                "ABC-123",
                "LOT1",
                savedRecord.getCheckInTime(),
                null
        ));

        // When
        ParkingRecordDto result = service.checkIn(dto);

        // Then
        assertEquals("ABC-123", result.licensePlate());
        assertEquals("LOT1", result.lotId());
        assertNull(result.checkOutTime());
        assertEquals(1, lot.getOccupiedSpaces());

        verify(parkingLotRepository).save(lot);
        verify(parkingRecordRepository).save(any(ParkingRecord.class));

    }

    @Test
    void checkIn_shouldThrowException_whenVehicleNotFound() {
        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT1");

        // Mock: vehicleRepository returns empty
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkIn(dto));
        // Then
        assertEquals("Vehicle not found", exception.getReason());

    }

    @Test
    void checkIn_shouldThrowException_whenLotNotFound() {
        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        // Mock: vehicle exists but lot does not
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkIn(dto));

        // Then
        assertEquals("Parking lot not found", exception.getReason());
    }


    @Test
    void checkIn_shouldThrowException_whenVehicleAlreadyInSameLot() {

        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");

        ParkingRecord activeRecord = new ParkingRecord();
        activeRecord.setVehicle(vehicle);
        activeRecord.setParkingLot(lot);

        // Mock
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));
        when(parkingRecordRepository.findByVehicleAndCheckOutTimeIsNull(vehicle)).thenReturn(Optional.of(activeRecord));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkIn(dto));

        assertEquals("Vehicle is already checked in this parking lot", exception.getReason());
    }

    @Test
    void checkIn_shouldThrowException_whenVehicleAlreadyCheckedInDifferentLot() {
        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT2");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot currentLot = new ParkingLot();
        currentLot.setLotId("LOT1");
        currentLot.setLocation("Main Street");

        ParkingLot newLot = new ParkingLot();
        newLot.setLotId("LOT2");

        ParkingRecord activeRecord = new ParkingRecord();
        activeRecord.setVehicle(vehicle);
        activeRecord.setParkingLot(currentLot);

        // Mock: vehicle is checked in another lot
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT2")).thenReturn(Optional.of(newLot));
        when(parkingRecordRepository.findByVehicleAndCheckOutTimeIsNull(vehicle))
                .thenReturn(Optional.of(activeRecord));

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkIn(dto));

        // Then
        assertEquals("Vehicle is already checked in parking lot: LOT1 (Main Street)", exception.getReason());
    }

    @Test
    void checkIn_shouldThrowException_whenLotIsFull() {
        // Given
        CheckInDto dto = new CheckInDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");
        lot.setCapacity(1);
        lot.setOccupiedSpaces(1); // lot is already full

        // Mock: vehicle exists, lot exists, no active record
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));
        when(parkingRecordRepository.findByVehicleAndCheckOutTimeIsNull(vehicle)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkIn(dto));

        // Then
        assertEquals("Parking lot is full", exception.getReason());

        // Verify that no records were saved
        verify(parkingRecordRepository, never()).save(any());
        verify(parkingLotRepository, never()).save(any());
    }

    // =======================
    // Tests for checkOut()
    // =======================
    @Test
    void checkOut_shouldSucceed_whenVehicleIsCheckedIn() {
        // Given
        CheckOutDto dto = new CheckOutDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");
        lot.setOccupiedSpaces(1);

        ParkingRecord activeRecord = ParkingRecord.builder()
                .vehicle(vehicle)
                .parkingLot(lot)
                .checkInTime(LocalDateTime.now().minusHours(2))
                .build();

        ParkingRecord savedRecord = ParkingRecord.builder()
                .vehicle(vehicle)
                .parkingLot(lot)
                .checkInTime(activeRecord.getCheckInTime())
                .checkOutTime(LocalDateTime.now())
                .build();

        // Mock repository calls
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));
        when(parkingRecordRepository.findByVehicleAndParkingLotAndCheckOutTimeIsNull(vehicle, lot))
                .thenReturn(Optional.of(activeRecord));
        when(parkingRecordRepository.save(activeRecord)).thenReturn(savedRecord);
        when(parkingRecordMapper.toDto(savedRecord)).thenReturn(new ParkingRecordDto(
                "ABC-123", "LOT1", savedRecord.getCheckInTime(), savedRecord.getCheckOutTime()
        ));

        // When
        ParkingRecordDto result = service.checkOut(dto);

        // Then
        assertEquals("ABC-123", result.licensePlate());
        assertEquals("LOT1", result.lotId());
        assertNotNull(result.checkOutTime());
        assertEquals(0, lot.getOccupiedSpaces()); // verify occupied spaces decreased

        verify(parkingLotRepository).save(lot);
        verify(parkingRecordRepository).save(activeRecord);
    }

    @Test
    void checkOut_shouldThrowException_whenVehicleNotFound() {
        // Given
        CheckOutDto dto = new CheckOutDto("ABC-123", "LOT1");

        //Mock
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkOut(dto));

        // Then
        assertEquals("Vehicle not found", exception.getReason());

        verify(parkingLotRepository, never()).findById(any());
        verify(parkingRecordRepository, never()).findByVehicleAndParkingLotAndCheckOutTimeIsNull(any(), any());
    }

    @Test
    void checkOut_shouldThrowException_whenLotNotFound() {
        // Given
        CheckOutDto dto = new CheckOutDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkOut(dto));

        // Then
        assertEquals("Parking lot not found", exception.getReason());

        verify(parkingRecordRepository, never()).findByVehicleAndParkingLotAndCheckOutTimeIsNull(any(), any());
    }

    @Test
    void checkOut_shouldThrowException_whenVehicleNotCheckedIn() {
        // Given
        CheckOutDto dto = new CheckOutDto("ABC-123", "LOT1");

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");

        ParkingLot lot = new ParkingLot();
        lot.setLotId("LOT1");

        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT1")).thenReturn(Optional.of(lot));
        when(parkingRecordRepository.findByVehicleAndParkingLotAndCheckOutTimeIsNull(vehicle, lot))
                .thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.checkOut(dto));

        // Then
        assertEquals("Vehicle is not currently checked in this parking lot", exception.getReason());
    }

}