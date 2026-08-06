package com.olalla.plantplan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olalla.plantplan.entity.SunExposure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Month;

public record CropSheetCreateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,

        @Size(max = 100, message = "La variedad no puede superar 100 caracteres")
        String variety,

        @JsonFormat(shape = JsonFormat.Shape.STRING) Month sowingStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month sowingEndMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month transplantStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month transplantEndMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month harvestStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month harvestEndMonth,

        @Positive(message = "La frecuencia de riego debe ser positiva")
        Integer wateringFrequencyDays,

        SunExposure sunExposure,

        String notes,

        @NotNull(message = "El usuario es obligatorio")
        Long userId
) {
}
