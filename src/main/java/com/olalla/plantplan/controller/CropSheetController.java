package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.CropSheetCreateRequest;
import com.olalla.plantplan.dto.CropSheetResponse;
import com.olalla.plantplan.dto.CropSheetUpdateRequest;
import com.olalla.plantplan.service.CropSheetService;
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
public class CropSheetController {

    private final CropSheetService cropSheetService;

    public CropSheetController(CropSheetService cropSheetService) {
        this.cropSheetService = cropSheetService;
    }

    @GetMapping("/crop-sheets")
    public List<CropSheetResponse> findAll() {
        return cropSheetService.findAll();
    }

    @GetMapping("/users/{userId}/crop-sheets")
    public List<CropSheetResponse> findByUserId(@PathVariable Long userId) {
        return cropSheetService.findByUserId(userId);
    }

    @GetMapping("/crop-sheets/{id}")
    public CropSheetResponse findById(@PathVariable Long id) {
        return cropSheetService.findById(id);
    }

    @PostMapping("/crop-sheets")
    @ResponseStatus(HttpStatus.CREATED)
    public CropSheetResponse create(@Valid @RequestBody CropSheetCreateRequest request) {
        return cropSheetService.create(request);
    }

    @PutMapping("/crop-sheets/{id}")
    public CropSheetResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CropSheetUpdateRequest request
    ) {
        return cropSheetService.update(id, request);
    }

    @DeleteMapping("/crop-sheets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cropSheetService.delete(id);
    }
}
