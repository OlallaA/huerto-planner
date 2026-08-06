package com.olalla.plantplan.dto;

import com.olalla.plantplan.entity.ReminderStatus;
import com.olalla.plantplan.entity.ReminderType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        ReminderType tipo,
        ReminderStatus estado,
        LocalDate scheduledDate,
        LocalDateTime completedAt,
        String title,
        String description,
        Long userId,
        Long cropSheetId,
        String cropSheetName,
        Long seedId,
        Long seedlingId,
        Long cropId,
        Long gardenId,
        String gardenName
) {
}
