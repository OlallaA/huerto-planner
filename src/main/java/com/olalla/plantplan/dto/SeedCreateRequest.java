package com.olalla.plantplan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SeedCreateRequest(
        @NotNull(message = "La quantity es obligatoria")
        @Positive(message = "La quantity debe ser positiva")
        Integer quantity,

        String notes,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long cropSheetId
) {
}
