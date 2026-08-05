package com.olalla.huertoplanner.dto;

import java.time.LocalDate;

public record PlantelResponse(
        Long id,
        Integer cantidadSembrada,
        LocalDate fechaSiembra,
        Integer cantidadTrasplantada,
        LocalDate fechaTrasplante,
        String observaciones,
        Long fichaCultivoId,
        String fichaCultivoNombre
) {
}
