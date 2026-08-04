package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.FichaCultivoCreateRequest;
import com.olalla.huertoplanner.dto.FichaCultivoResponse;
import com.olalla.huertoplanner.dto.FichaCultivoUpdateRequest;
import com.olalla.huertoplanner.service.FichaCultivoService;
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
public class FichaCultivoController {

    private final FichaCultivoService fichaCultivoService;

    public FichaCultivoController(FichaCultivoService fichaCultivoService) {
        this.fichaCultivoService = fichaCultivoService;
    }

    @GetMapping("/fichas-cultivo")
    public List<FichaCultivoResponse> findAll() {
        return fichaCultivoService.findAll();
    }

    @GetMapping("/usuarios/{usuarioId}/fichas-cultivo")
    public List<FichaCultivoResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return fichaCultivoService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/fichas-cultivo/{id}")
    public FichaCultivoResponse findById(@PathVariable Long id) {
        return fichaCultivoService.findById(id);
    }

    @PostMapping("/fichas-cultivo")
    @ResponseStatus(HttpStatus.CREATED)
    public FichaCultivoResponse create(@Valid @RequestBody FichaCultivoCreateRequest request) {
        return fichaCultivoService.create(request);
    }

    @PutMapping("/fichas-cultivo/{id}")
    public FichaCultivoResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FichaCultivoUpdateRequest request
    ) {
        return fichaCultivoService.update(id, request);
    }

    @DeleteMapping("/fichas-cultivo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        fichaCultivoService.delete(id);
    }
}
