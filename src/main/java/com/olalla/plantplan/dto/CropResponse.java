package com.olalla.plantplan.dto;

import java.time.LocalDate;

public record CropResponse(
        Long id,
        Integer initialQuantity,
        LocalDate transplantDate,
        Integer harvestedQuantity,
        LocalDate endDate,
        String notes,
        Long cropSheetId,
        String cropSheetName,
        Long gardenId,
        String gardenName
) {
}
