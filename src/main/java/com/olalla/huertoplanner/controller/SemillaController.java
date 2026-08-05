package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.SemillaCreateRequest;
import com.olalla.huertoplanner.dto.SemillaResponse;
import com.olalla.huertoplanner.dto.SemillaUpdateRequest;
import com.olalla.huertoplanner.service.SemillaService;
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
public class SemillaController {

    private final SemillaService semillaService;

    public SemillaController(SemillaService semillaService) {
        this.semillaService = semillaService;
    }

    @GetMapping("/semillas")
    public List<SemillaResponse> findAll() {
        return semillaService.findAll();
    }

    @GetMapping("/fichas-cultivo/{fichaCultivoId}/semillas")
    public List<SemillaResponse> findByFichaCultivoId(@PathVariable Long fichaCultivoId) {
        return semillaService.findByFichaCultivoId(fichaCultivoId);
    }

    @GetMapping("/semillas/{id}")
    public SemillaResponse findById(@PathVariable Long id) {
        return semillaService.findById(id);
    }

    @PostMapping("/semillas")
    @ResponseStatus(HttpStatus.CREATED)
    public SemillaResponse create(@Valid @RequestBody SemillaCreateRequest request) {
        return semillaService.create(request);
    }

    @PutMapping("/semillas/{id}")
    public SemillaResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SemillaUpdateRequest request
    ) {
        return semillaService.update(id, request);
    }

    @DeleteMapping("/semillas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        semillaService.delete(id);
    }
}
