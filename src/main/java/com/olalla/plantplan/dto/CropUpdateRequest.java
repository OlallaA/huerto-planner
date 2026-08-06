package com.olalla.plantplan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record CropUpdateRequest(
        @NotNull(message = "La quantity inicial es obligatoria")
        @Positive(message = "La quantity inicial debe ser positiva")
        Integer initialQuantity,

        LocalDate transplantDate,

        @PositiveOrZero(message = "La quantity cosechada no puede ser negativa")
        Integer harvestedQuantity,

        LocalDate endDate,

        String notes,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long cropSheetId,

        @NotNull(message = "El garden es obligatorio")
        Long gardenId
) {
}
