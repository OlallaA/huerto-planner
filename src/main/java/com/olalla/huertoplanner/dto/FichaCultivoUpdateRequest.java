package com.olalla.huertoplanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olalla.huertoplanner.entity.ExposicionSolar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Month;

public record FichaCultivoUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 100, message = "La variedad no puede superar 100 caracteres")
        String variedad,

        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioSiembra,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinSiembra,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioTrasplante,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinTrasplante,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesInicioCosecha,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month mesFinCosecha,

        @Positive(message = "La frecuencia de riego debe ser positiva")
        Integer frecuenciaRiego,

        ExposicionSolar exposicionSolar,

        String observaciones,

        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId
) {
}
