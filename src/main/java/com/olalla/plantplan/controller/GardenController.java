package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.GardenCreateRequest;
import com.olalla.plantplan.dto.GardenResponse;
import com.olalla.plantplan.dto.GardenUpdateRequest;
import com.olalla.plantplan.service.GardenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public List<GardenResponse> findAll() {
        return gardenService.findAll();
    }

    @GetMapping("/users/{userId}/gardens")
    public List<GardenResponse> findByUserId(@PathVariable Long userId) {
        return gardenService.findByUserId(userId);
    }

    @GetMapping("/gardens/{id}")
    public GardenResponse findById(@PathVariable Long id) {
        return gardenService.findById(id);
    }

    @PostMapping("/gardens")
    @ResponseStatus(HttpStatus.CREATED)
    public GardenResponse create(@Valid @RequestBody GardenCreateRequest request) {
        return gardenService.create(request);
    }

    @PutMapping("/gardens/{id}")
    public GardenResponse update(@PathVariable Long id, @Valid @RequestBody GardenUpdateRequest request) {
        return gardenService.update(id, request);
    }

    @DeleteMapping("/gardens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        gardenService.delete(id);
    }
}
