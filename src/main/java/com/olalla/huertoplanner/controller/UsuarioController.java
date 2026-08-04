package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.UsuarioCreateRequest;
import com.olalla.huertoplanner.dto.UsuarioResponse;
import com.olalla.huertoplanner.dto.UsuarioUpdateRequest;
import com.olalla.huertoplanner.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse create(@Valid @RequestBody UsuarioCreateRequest request) {
        return usuarioService.create(request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse update(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request) {
        return usuarioService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }
}
