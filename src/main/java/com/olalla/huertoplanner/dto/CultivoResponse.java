package com.olalla.huertoplanner.dto;

import java.time.LocalDate;

public record CultivoResponse(
        Long id,
        Integer cantidadInicial,
        LocalDate fechaTrasplante,
        Integer cantidadCosechada,
        LocalDate fechaFinCultivo,
        String observaciones,
        Long fichaCultivoId,
        String fichaCultivoNombre,
        Long huertoId,
        String huertoNombre
) {
}
