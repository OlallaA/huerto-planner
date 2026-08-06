package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.ReminderResponse;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.ReminderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/users/{userId}/reminders")
    public List<ReminderResponse> findByUserAndRange(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (!user.id().equals(userId)) {
            throw new ForbiddenException("No puedes acceder a los recordatorios de otro usuario");
        }

        return reminderService.findByUserAndRange(userId, from, to);
    }

    @GetMapping("/reminders/{id}")
    public ReminderResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return reminderService.findById(user.id(), id);
    }

    @PostMapping("/reminders/{id}/complete")
    public ReminderResponse complete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return reminderService.complete(user.id(), id);
    }
}
