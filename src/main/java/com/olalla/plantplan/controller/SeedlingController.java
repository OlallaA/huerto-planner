package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.SeedlingCreateRequest;
import com.olalla.plantplan.dto.SeedlingResponse;
import com.olalla.plantplan.dto.SeedlingUpdateRequest;
import com.olalla.plantplan.service.SeedlingService;
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
public class SeedlingController {

    private final SeedlingService seedlingService;

    public SeedlingController(SeedlingService seedlingService) {
        this.seedlingService = seedlingService;
    }

    @GetMapping("/seedlings")
    public List<SeedlingResponse> findAll() {
        return seedlingService.findAll();
    }

    @GetMapping("/crop-sheets/{cropSheetId}/seedlings")
    public List<SeedlingResponse> findByCropSheetId(@PathVariable Long cropSheetId) {
        return seedlingService.findByCropSheetId(cropSheetId);
    }

    @GetMapping("/seedlings/{id}")
    public SeedlingResponse findById(@PathVariable Long id) {
        return seedlingService.findById(id);
    }

    @PostMapping("/seedlings")
    @ResponseStatus(HttpStatus.CREATED)
    public SeedlingResponse create(@Valid @RequestBody SeedlingCreateRequest request) {
        return seedlingService.create(request);
    }

    @PutMapping("/seedlings/{id}")
    public SeedlingResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SeedlingUpdateRequest request
    ) {
        return seedlingService.update(id, request);
    }

    @DeleteMapping("/seedlings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        seedlingService.delete(id);
    }
}
