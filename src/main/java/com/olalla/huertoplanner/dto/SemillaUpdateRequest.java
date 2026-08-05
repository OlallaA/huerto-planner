package com.olalla.huertoplanner.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SemillaUpdateRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser positiva")
        Integer cantidad,

        String observaciones,

        @NotNull(message = "La ficha de cultivo es obligatoria")
        Long fichaCultivoId
) {
}
