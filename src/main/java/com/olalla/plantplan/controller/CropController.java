package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.CropCreateRequest;
import com.olalla.plantplan.dto.CropResponse;
import com.olalla.plantplan.dto.CropUpdateRequest;
import com.olalla.plantplan.service.CropService;
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
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping("/crops")
    public List<CropResponse> findAll() {
        return cropService.findAll();
    }

    @GetMapping("/gardens/{gardenId}/crops")
    public List<CropResponse> findByGardenId(@PathVariable Long gardenId) {
        return cropService.findByGardenId(gardenId);
    }

    @GetMapping("/crop-sheets/{cropSheetId}/crops")
    public List<CropResponse> findByCropSheetId(@PathVariable Long cropSheetId) {
        return cropService.findByCropSheetId(cropSheetId);
    }

    @GetMapping("/crops/{id}")
    public CropResponse findById(@PathVariable Long id) {
        return cropService.findById(id);
    }

    @PostMapping("/crops")
    @ResponseStatus(HttpStatus.CREATED)
    public CropResponse create(@Valid @RequestBody CropCreateRequest request) {
        return cropService.create(request);
    }

    @PutMapping("/crops/{id}")
    public CropResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CropUpdateRequest request
    ) {
        return cropService.update(id, request);
    }

    @DeleteMapping("/crops/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cropService.delete(id);
    }
}
