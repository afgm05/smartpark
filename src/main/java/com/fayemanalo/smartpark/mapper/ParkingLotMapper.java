package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.model.ParkingLot;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapper {

    // Convert DTO to Entity
    public ParkingLot toEntity(ParkingLotDto dto) {
        if (dto == null) return null;

        return ParkingLot.builder()
                .lotId(dto.lotId())
                .location(dto.location())
                .capacity(dto.capacity())
                .occupiedSpaces(dto.occupiedSpaces())
                .build();
    }

    // Convert Entity to DTO
    public ParkingLotDto toDto(ParkingLot entity) {
        if (entity == null) return null;

        return ParkingLotDto.builder()
                .lotId(entity.getLotId())
                .location(entity.getLocation())
                .capacity(entity.getCapacity())
                .occupiedSpaces(entity.getOccupiedSpaces())
                .build();
    }
}
