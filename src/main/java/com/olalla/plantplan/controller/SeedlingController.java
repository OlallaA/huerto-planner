package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.SeedlingCreateRequest;
import com.olalla.plantplan.dto.SeedlingResponse;
import com.olalla.plantplan.dto.SeedlingUpdateRequest;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.SeedlingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeedlingController {

    private final SeedlingService seedlingService;

    public SeedlingController(SeedlingService seedlingService) {
        this.seedlingService = seedlingService;
    }

    @GetMapping("/seedlings")
    public List<SeedlingResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return seedlingService.findByUserId(user.id());
    }

    @GetMapping("/crop-sheets/{cropSheetId}/seedlings")
    public List<SeedlingResponse> findByCropSheetId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cropSheetId
    ) {
        return seedlingService.findByCropSheetId(user.id(), cropSheetId);
    }

    @GetMapping("/seedlings/{id}")
    public SeedlingResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return seedlingService.findById(user.id(), id);
    }

    @PostMapping("/seedlings")
    @ResponseStatus(HttpStatus.CREATED)
    public SeedlingResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody SeedlingCreateRequest request
    ) {
        return seedlingService.create(user.id(), request);
    }

    @PutMapping("/seedlings/{id}")
    public SeedlingResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody SeedlingUpdateRequest request
    ) {
        return seedlingService.update(user.id(), id, request);
    }

    @DeleteMapping("/seedlings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        seedlingService.delete(user.id(), id);
    }
}