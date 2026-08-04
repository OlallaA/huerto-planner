package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.CultivoCreateRequest;
import com.olalla.huertoplanner.dto.CultivoResponse;
import com.olalla.huertoplanner.dto.CultivoUpdateRequest;
import com.olalla.huertoplanner.service.CultivoService;
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
public class CultivoController {

    private final CultivoService cultivoService;

    public CultivoController(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
    }

    @GetMapping("/cultivos")
    public List<CultivoResponse> findAll() {
        return cultivoService.findAll();
    }

    @GetMapping("/huertos/{huertoId}/cultivos")
    public List<CultivoResponse> findByHuertoId(@PathVariable Long huertoId) {
        return cultivoService.findByHuertoId(huertoId);
    }

    @GetMapping("/fichas-cultivo/{fichaCultivoId}/cultivos")
    public List<CultivoResponse> findByFichaCultivoId(@PathVariable Long fichaCultivoId) {
        return cultivoService.findByFichaCultivoId(fichaCultivoId);
    }

    @GetMapping("/cultivos/{id}")
    public CultivoResponse findById(@PathVariable Long id) {
        return cultivoService.findById(id);
    }

    @PostMapping("/cultivos")
    @ResponseStatus(HttpStatus.CREATED)
    public CultivoResponse create(@Valid @RequestBody CultivoCreateRequest request) {
        return cultivoService.create(request);
    }

    @PutMapping("/cultivos/{id}")
    public CultivoResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CultivoUpdateRequest request
    ) {
        return cultivoService.update(id, request);
    }

    @DeleteMapping("/cultivos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cultivoService.delete(id);
    }
}
