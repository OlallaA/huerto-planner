package com.olalla.huertoplanner.dto;

public record SemillaResponse(
        Long id,
        Integer cantidad,
        String observaciones,
        Long fichaCultivoId,
        String fichaCultivoNombre
) {
}
