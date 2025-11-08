package com.fayemanalo.smartpark.repository;

import com.fayemanalo.smartpark.model.ParkingLot;
import com.fayemanalo.smartpark.model.ParkingRecord;
import com.fayemanalo.smartpark.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {

    // Checks if there is an active ParkingRecord for a given vehicle
    boolean existsByVehicleAndCheckOutTimeIsNull(Vehicle vehicle);

    // Find the active parking record for a vehicle (used for message with lot info)
    Optional<ParkingRecord> findByVehicleAndCheckOutTimeIsNull(Vehicle vehicle);

    // Find the active parking record for a vehicle in a specific lot
    Optional<ParkingRecord> findByVehicleAndParkingLotAndCheckOutTimeIsNull(Vehicle vehicle, ParkingLot parkingLot);
}
