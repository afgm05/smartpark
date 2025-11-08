package com.fayemanalo.smartpark.repository;

import com.fayemanalo.smartpark.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, String> {
}
