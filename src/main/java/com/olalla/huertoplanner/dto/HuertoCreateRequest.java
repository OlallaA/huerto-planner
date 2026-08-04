package com.olalla.huertoplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HuertoCreateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 150, message = "La ubicacion no puede superar 150 caracteres")
        String ubicacion,

        Double latitud,

        Double longitud,

        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId
) {
}
