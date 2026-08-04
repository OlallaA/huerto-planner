package com.olalla.huertoplanner.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record CultivoCreateRequest(
        @NotNull(message = "La cantidad inicial es obligatoria")
        @Positive(message = "La cantidad inicial debe ser positiva")
        Integer cantidadInicial,

        LocalDate fechaTrasplante,

        @PositiveOrZero(message = "La cantidad cosechada no puede ser negativa")
        Integer cantidadCosechada,

        LocalDate fechaFinCultivo,

        String observaciones,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long fichaCultivoId,

        @NotNull(message = "El huerto es obligatorio")
        Long huertoId
) {
}
