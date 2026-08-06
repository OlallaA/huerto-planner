package com.olalla.plantplan.dto;

public record SeedResponse(
        Long id,
        Integer quantity,
        String notes,
        Long cropSheetId,
        String cropSheetName
) {
}
