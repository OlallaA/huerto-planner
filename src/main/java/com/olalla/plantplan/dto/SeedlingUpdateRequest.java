package com.olalla.plantplan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record SeedlingUpdateRequest(
        @NotNull(message = "La quantity sembrada es obligatoria")
        @Positive(message = "La quantity sembrada debe ser positiva")
        Integer sownQuantity,

        LocalDate sowingDate,

        @PositiveOrZero(message = "La quantity trasplantada no puede ser negativa")
        Integer transplantedQuantity,

        LocalDate transplantDate,

        String notes,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long cropSheetId
) {
}
