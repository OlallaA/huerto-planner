package com.olalla.huertoplanner.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record PlantelUpdateRequest(
        @NotNull(message = "La cantidad sembrada es obligatoria")
        @Positive(message = "La cantidad sembrada debe ser positiva")
        Integer cantidadSembrada,

        LocalDate fechaSiembra,

        @PositiveOrZero(message = "La cantidad trasplantada no puede ser negativa")
        Integer cantidadTrasplantada,

        LocalDate fechaTrasplante,

        String observaciones,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long fichaCultivoId
) {
}
