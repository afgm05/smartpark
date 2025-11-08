package com.fayemanalo.smartpark.mapper;

import com.fayemanalo.smartpark.dto.ParkingLotDto;
import com.fayemanalo.smartpark.model.ParkingLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotMapperTest {

    private ParkingLotMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ParkingLotMapper();
    }

    // =======================
    // Tests for toEntity()
    // =======================
    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        ParkingLotDto dto = ParkingLotDto.builder()
                .lotId("LOT1")
                .location("Main Street")
                .capacity(10)
                .occupiedSpaces(2)
                .build();

        // When
        ParkingLot entity = mapper.toEntity(dto);

        // Then
        assertEquals("LOT1", entity.getLotId());
        assertEquals("Main Street", entity.getLocation());
        assertEquals(10, entity.getCapacity());
        assertEquals(2, entity.getOccupiedSpaces());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        // When
        ParkingLot entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    // =======================
    // Tests for toDto()
    // =======================
    @Test
    void toDto_shouldMapEntityToDto() {
        // Given
        ParkingLot entity = ParkingLot.builder()
                .lotId("LOT1")
                .location("Main Street")
                .capacity(10)
                .occupiedSpaces(2)
                .build();

        // When
        ParkingLotDto dto = mapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals("LOT1", dto.lotId());
        assertEquals("Main Street", dto.location());
        assertEquals(10, dto.capacity());
        assertEquals(2, dto.occupiedSpaces());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        // When
        ParkingLotDto dto = mapper.toDto(null);

        // Then
        assertNull(dto);
    }

}