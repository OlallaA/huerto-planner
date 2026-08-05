package com.olalla.huertoplanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olalla.huertoplanner.entity.ExposicionSolar;

import java.time.Month;

public record FichaCultivoResponse(
        Long id,
        String nombre,
        String variedad,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioSiembra,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinSiembra,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioTrasplante,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinTrasplante,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioCosecha,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinCosecha,
        Integer frecuenciaRiego,
        ExposicionSolar exposicionSolar,
        String observaciones,
        Long usuarioId,
        String usuarioNombre
) {
}
