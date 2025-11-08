package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.VehicleDto;
import com.fayemanalo.smartpark.model.Vehicle;
import com.fayemanalo.smartpark.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private VehicleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VehicleMapper();
    }

    // ====================
    // Tests for toEntity()
    // ====================
    @Test
    void toEntity_shouldMapDtoToEntityCorrectly() {
        // Given
        VehicleDto dto = VehicleDto.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Alice Smith")
                .build();

        // When
        Vehicle entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals("ABC-123", entity.getLicensePlate());
        assertEquals(VehicleType.CAR, entity.getType());
        assertEquals("Alice Smith", entity.getOwnerName());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        // When
        Vehicle entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    // ====================
    // Tests for toDto()
    // ====================
    @Test
    void toDto_shouldMapEntityToDtoCorrectly() {
        // Given
        Vehicle entity = Vehicle.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Alice Smith")
                .build();

        // When
        VehicleDto dto = mapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals("ABC-123", dto.licensePlate());
        assertEquals(VehicleType.CAR, dto.type());
        assertEquals("Alice Smith", dto.ownerName());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        // When
        VehicleDto dto = mapper.toDto(null);

        // Then
        assertNull(dto);
    }

}