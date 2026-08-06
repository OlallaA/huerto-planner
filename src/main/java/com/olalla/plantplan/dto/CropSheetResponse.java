package com.olalla.plantplan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olalla.plantplan.entity.SunExposure;

import java.time.Month;

public record CropSheetResponse(
        Long id,
        String name,
        String variety,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month sowingStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month sowingEndMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month transplantStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month transplantEndMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month harvestStartMonth,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Month harvestEndMonth,
        Integer wateringFrequencyDays,
        SunExposure sunExposure,
        String notes,
        Long userId,
        String userName
) {
}
