package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    // Convert DTO to Entity
    public Vehicle toEntity(VehicleDto dto) {
        if (dto == null) return null;

        return Vehicle.builder()
                .licensePlate(dto.licensePlate())
                .type(dto.type())
                .ownerName(dto.ownerName())
                .build();
    }

    // Convert Entity to DTO
    public VehicleDto toDto(Vehicle entity) {
        if (entity == null) return null;

        return VehicleDto.builder()
                .licensePlate(entity.getLicensePlate())
                .type(entity.getType())
                .ownerName(entity.getOwnerName())
                .build();
    }
}
