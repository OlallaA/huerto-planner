package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.RecordatorioResponse;
import com.olalla.huertoplanner.service.RecordatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    public RecordatorioController(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    @GetMapping("/usuarios/{usuarioId}/recordatorios")
    public List<RecordatorioResponse> findByUsuarioAndRango(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return recordatorioService.findByUsuarioAndRango(usuarioId, desde, hasta);
    }

    @GetMapping("/recordatorios/{id}")
    public RecordatorioResponse findById(@PathVariable Long id) {
        return recordatorioService.findById(id);
    }

    @PostMapping("/recordatorios/{id}/completar")
    public RecordatorioResponse completar(@PathVariable Long id) {
        return recordatorioService.completar(id);
    }
}
