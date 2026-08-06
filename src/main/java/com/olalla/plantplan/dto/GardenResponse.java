package com.olalla.plantplan.dto;

public record GardenResponse(
        Long id,
        String name,
        String location,
        Double latitude,
        Double longitude,
        Long userId,
        String userName
) {
}
