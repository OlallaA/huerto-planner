package com.olalla.plantplan.dto;

import java.time.LocalDate;

public record SeedlingResponse(
        Long id,
        Integer sownQuantity,
        LocalDate sowingDate,
        Integer transplantedQuantity,
        LocalDate transplantDate,
        String notes,
        Long cropSheetId,
        String cropSheetName
) {
}
