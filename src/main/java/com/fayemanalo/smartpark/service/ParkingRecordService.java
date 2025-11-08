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
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for handling parking operations such as check-in and check-out.
 * Responsible for managing ParkingRecords, updating occupied spaces, and enforcing business rules.
 */
@Service
@Transactional
public class ParkingRecordService {

    private final ParkingRecordRepository parkingRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingRecordMapper parkingRecordMapper;

    public ParkingRecordService(ParkingRecordRepository parkingRecordRepository,
                                VehicleRepository vehicleRepository,
                                ParkingLotRepository parkingLotRepository, ParkingRecordMapper parkingRecordMapper) {
        this.parkingRecordRepository = parkingRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.parkingRecordMapper = parkingRecordMapper;
    }

    /**
     * Checks in a vehicle into a specified parking lot.
     *
     * Business rules enforced:
     * 1. Vehicle must exist.
     * 2. Parking lot must exist.
     * 3. Vehicle cannot already be checked in another lot.
     * 4. Parking lot must have available spaces.
     *
     * @param dto Data transfer object containing license plate and lot ID
     * @return ParkingRecordDto containing check-in information
     * @throws ResponseStatusException if any validation fails (e.g., vehicle not found, lot full)
     */
    public ParkingRecordDto checkIn(CheckInDto dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.licensePlate())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found"));

        ParkingLot lot = parkingLotRepository.findById(dto.lotId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Parking lot not found"));

        // Check if vehicle is checked in a parking lot
        Optional<ParkingRecord> activeRecord = parkingRecordRepository
                .findByVehicleAndCheckOutTimeIsNull(vehicle);

        // If vehicle is already checked in, return exception messages
        if (activeRecord.isPresent()) {
            ParkingLot currentLot = activeRecord.get().getParkingLot();

            if (currentLot.getLotId().equals(dto.lotId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Vehicle is already checked in this parking lot"
                );
            } else {
                String currentLotInfo = currentLot.getLotId() + " (" + currentLot.getLocation() + ")";
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Vehicle is already checked in parking lot: " + currentLotInfo
                );
            }
        }

        if (lot.getOccupiedSpaces() >= lot.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parking lot is full");
        }

        ParkingRecord record = ParkingRecord.builder()
                .vehicle(vehicle)
                .parkingLot(lot)
                .checkInTime(LocalDateTime.now())
                .build();

        // Update lot occupied spaces
        lot.setOccupiedSpaces(lot.getOccupiedSpaces() + 1);
        parkingLotRepository.save(lot);

        ParkingRecord savedRecord = parkingRecordRepository.save(record);

        return parkingRecordMapper.toDto(savedRecord);
    }

    /**
     * Checks out a vehicle from a specified parking lot.
     *
     * Business rules enforced:
     * 1. Vehicle must exist.
     * 2. Parking lot must exist.
     * 3. Vehicle must be actively checked in to the specified lot.
     *
     * @param dto Data transfer object containing license plate and lot ID
     * @return ParkingRecordDto containing check-out information
     * @throws ResponseStatusException if any validation fails (e.g., vehicle not checked in)
     */
    public ParkingRecordDto checkOut(CheckOutDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.licensePlate())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found"));

        ParkingLot lot = parkingLotRepository.findById(dto.lotId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Parking lot not found"));

        // Find vehicle's active record in the specified lot
        ParkingRecord activeRecord = parkingRecordRepository
                .findByVehicleAndParkingLotAndCheckOutTimeIsNull(vehicle, lot)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Vehicle is not currently checked in this parking lot"));

        // Set check-out time
        activeRecord.setCheckOutTime(LocalDateTime.now());
        ParkingRecord savedRecord = parkingRecordRepository.save(activeRecord);

        // Update lot occupied spaces
        lot.setOccupiedSpaces(lot.getOccupiedSpaces() - 1);
        parkingLotRepository.save(lot);

        return parkingRecordMapper.toDto(savedRecord);
    }

}
