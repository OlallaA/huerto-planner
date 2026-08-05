package com.olalla.huertoplanner.controller;

import com.olalla.huertoplanner.dto.PlantelCreateRequest;
import com.olalla.huertoplanner.dto.PlantelResponse;
import com.olalla.huertoplanner.dto.PlantelUpdateRequest;
import com.olalla.huertoplanner.service.PlantelService;
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
public class PlantelController {

    private final PlantelService plantelService;

    public PlantelController(PlantelService plantelService) {
        this.plantelService = plantelService;
    }

    @GetMapping("/planteles")
    public List<PlantelResponse> findAll() {
        return plantelService.findAll();
    }

    @GetMapping("/fichas-cultivo/{fichaCultivoId}/planteles")
    public List<PlantelResponse> findByFichaCultivoId(@PathVariable Long fichaCultivoId) {
        return plantelService.findByFichaCultivoId(fichaCultivoId);
    }

    @GetMapping("/planteles/{id}")
    public PlantelResponse findById(@PathVariable Long id) {
        return plantelService.findById(id);
    }

    @PostMapping("/planteles")
    @ResponseStatus(HttpStatus.CREATED)
    public PlantelResponse create(@Valid @RequestBody PlantelCreateRequest request) {
        return plantelService.create(request);
    }

    @PutMapping("/planteles/{id}")
    public PlantelResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PlantelUpdateRequest request
    ) {
        return plantelService.update(id, request);
    }

    @DeleteMapping("/planteles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        plantelService.delete(id);
    }
}
