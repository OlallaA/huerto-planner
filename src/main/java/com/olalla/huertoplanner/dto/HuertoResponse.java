package com.olalla.huertoplanner.dto;

public record HuertoResponse(
        Long id,
        String nombre,
        String ubicacion,
        Double latitud,
        Double longitud,
        Long usuarioId,
        String usuarioNombre
) {
}
