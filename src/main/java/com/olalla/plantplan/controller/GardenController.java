package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.GardenCreateRequest;
import com.olalla.plantplan.dto.GardenResponse;
import com.olalla.plantplan.dto.GardenUpdateRequest;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.GardenService;
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
public class GardenController {

    private final GardenService gardenService;

    public GardenController(GardenService gardenService) {
        this.gardenService = gardenService;
    }

    @GetMapping("/gardens")
    public List<GardenResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return gardenService.findByUserId(user.id());
    }

    @GetMapping("/users/{userId}/gardens")
    public List<GardenResponse> findByUserId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long userId
    ) {
        if (!user.id().equals(userId)) {
            throw new ForbiddenException("No puedes acceder a los huertos de otro usuario");
        }

        return gardenService.findByUserId(userId);
    }

    @GetMapping("/gardens/{id}")
    public GardenResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return gardenService.findById(user.id(), id);
    }

    @PostMapping("/gardens")
    @ResponseStatus(HttpStatus.CREATED)
    public GardenResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody GardenCreateRequest request
    ) {
        return gardenService.create(user.id(), request);
    }

    @PutMapping("/gardens/{id}")
    public GardenResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody GardenUpdateRequest request
    ) {
        return gardenService.update(user.id(), id, request);
    }

    @DeleteMapping("/gardens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        gardenService.delete(user.id(), id);
    }
}
