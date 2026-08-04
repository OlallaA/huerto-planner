package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.HuertoCreateRequest;
import com.olalla.huertoplanner.dto.HuertoResponse;
import com.olalla.huertoplanner.dto.HuertoUpdateRequest;
import com.olalla.huertoplanner.service.HuertoService;
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
public class HuertoController {

    private final HuertoService huertoService;

    public HuertoController(HuertoService huertoService) {
        this.huertoService = huertoService;
    }

    @GetMapping("/huertos")
    public List<HuertoResponse> findAll() {
        return huertoService.findAll();
    }

    @GetMapping("/usuarios/{usuarioId}/huertos")
    public List<HuertoResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return huertoService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/huertos/{id}")
    public HuertoResponse findById(@PathVariable Long id) {
        return huertoService.findById(id);
    }

    @PostMapping("/huertos")
    @ResponseStatus(HttpStatus.CREATED)
    public HuertoResponse create(@Valid @RequestBody HuertoCreateRequest request) {
        return huertoService.create(request);
    }

    @PutMapping("/huertos/{id}")
    public HuertoResponse update(@PathVariable Long id, @Valid @RequestBody HuertoUpdateRequest request) {
        return huertoService.update(id, request);
    }

    @DeleteMapping("/huertos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        huertoService.delete(id);
    }
}
