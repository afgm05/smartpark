package com.fayemanalo.smartpark.service;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.dto.ParkingLotStatusDto;
import com.fayemanalo.smartpark.dto.VehiclesInLotDto;
import com.fayemanalo.smartpark.mapper.ParkingLotMapper;
import com.fayemanalo.smartpark.model.ParkingLot;
import com.fayemanalo.smartpark.repository.ParkingLotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, ParkingLotMapper parkingLotMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
    }

    /**
     * Save a new parking lot to the database.
     *
     * @param parkingLotDto the DTO containing parking lot information to be saved
     * @return the saved ParkingLotDto reflecting the persisted entity
     * @throws ResponseStatusException if a parking lot with the same ID already exists
     */
    public ParkingLotDto save(ParkingLotDto parkingLotDto) {
        var parkingLot = parkingLotMapper.toEntity(parkingLotDto);

        // Check if parking lot already exists
        if (parkingLotRepository.existsById(parkingLot.getLotId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Parking lot with this ID already exists"
            );
        }

        var savedLot = parkingLotRepository.save(parkingLot);
        return parkingLotMapper.toDto(savedLot);
    }

    /**
     * Retrieves the status of a parking lot by its ID.
     *
     * The status includes the lot ID, location, total capacity, number of occupied spaces,
     * and the number of available spaces.
     *
     * @param lotId the ID of the parking lot to retrieve
     * @return a {@link ParkingLotStatusDto} containing the parking lot's status information
     * @throws ResponseStatusException if the parking lot with the given ID is not found,
     *         returning HTTP status 404 (NOT_FOUND)
     */
    public ParkingLotStatusDto getStatus(String lotId) {

        var lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking lot not found"
                ));

        int availableSpaces = lot.getCapacity() - lot.getOccupiedSpaces();

        return new ParkingLotStatusDto(
                lot.getLotId(),
                lot.getLocation(),
                lot.getCapacity(),
                lot.getOccupiedSpaces(),
                availableSpaces
        );
    }

    /**
     * Retrieves a list of vehicles currently parked in the specified parking lot.
     *
     * This method only includes vehicles that have checked in and have not yet checked out.
     *
     * @param lotId the ID of the parking lot to query
     * @return a list of VehiclesInLotDto representing the vehicles currently in the parking lot
     * @throws ResponseStatusException if the parking lot with the given ID does not exist
     */
    public List<VehiclesInLotDto> getVehiclesInLot(String lotId) {
        ParkingLot lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking lot not found"
                ));

        return lot.getParkingRecords().stream()
                .filter(record -> record.getCheckOutTime() == null)
                .map(record -> new VehiclesInLotDto(
                        record.getVehicle().getLicensePlate(),
                        record.getVehicle().getType(),
                        record.getVehicle().getOwnerName(),
                        record.getCheckInTime()
                ))
                .collect(Collectors.toList());
    }
}
