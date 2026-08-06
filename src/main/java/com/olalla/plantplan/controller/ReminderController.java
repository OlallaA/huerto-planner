package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.ReminderResponse;
import com.olalla.plantplan.service.ReminderService;
import org.springframework.format.annotation.DateTimeFormat;
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
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reminderService.findByUserAndRange(userId, from, to);
    }

    @GetMapping("/reminders/{id}")
    public ReminderResponse findById(@PathVariable Long id) {
        return reminderService.findById(id);
    }

    @PostMapping("/reminders/{id}/complete")
    public ReminderResponse complete(@PathVariable Long id) {
        return reminderService.complete(id);
    }
}
