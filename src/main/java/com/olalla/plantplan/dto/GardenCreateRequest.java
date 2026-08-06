package com.olalla.plantplan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GardenCreateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,

        @Size(max = 150, message = "La location no puede superar 150 caracteres")
        String location,

        Double latitude,

        Double longitude,

        @NotNull(message = "El usuario es obligatorio")
        Long userId
) {
}
