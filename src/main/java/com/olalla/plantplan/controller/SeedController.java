package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.SeedCreateRequest;
import com.olalla.plantplan.dto.SeedResponse;
import com.olalla.plantplan.dto.SeedUpdateRequest;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.SeedService;
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
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) {
        this.seedService = seedService;
    }

    @GetMapping("/seeds")
    public List<SeedResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return seedService.findByUserId(user.id());
    }

    @GetMapping("/crop-sheets/{cropSheetId}/seeds")
    public List<SeedResponse> findByCropSheetId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cropSheetId
    ) {
        return seedService.findByCropSheetId(user.id(), cropSheetId);
    }

    @GetMapping("/seeds/{id}")
    public SeedResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return seedService.findById(user.id(), id);
    }

    @PostMapping("/seeds")
    @ResponseStatus(HttpStatus.CREATED)
    public SeedResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody SeedCreateRequest request
    ) {
        return seedService.create(user.id(), request);
    }

    @PutMapping("/seeds/{id}")
    public SeedResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody SeedUpdateRequest request
    ) {
        return seedService.update(user.id(), id, request);
    }

    @DeleteMapping("/seeds/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        seedService.delete(user.id(), id);
    }
}