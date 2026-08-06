package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.CropCreateRequest;
import com.olalla.plantplan.dto.CropResponse;
import com.olalla.plantplan.dto.CropUpdateRequest;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.CropService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<CropResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return cropService.findByUserId(user.id());
    }

    @GetMapping("/gardens/{gardenId}/crops")
    public List<CropResponse> findByGardenId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long gardenId
    ) {
        return cropService.findByGardenId(user.id(), gardenId);
    }

    @GetMapping("/crop-sheets/{cropSheetId}/crops")
    public List<CropResponse> findByCropSheetId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cropSheetId
    ) {
        return cropService.findByCropSheetId(user.id(), cropSheetId);
    }

    @GetMapping("/crops/{id}")
    public CropResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return cropService.findById(user.id(), id);
    }

    @PostMapping("/crops")
    @ResponseStatus(HttpStatus.CREATED)
    public CropResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CropCreateRequest request
    ) {
        return cropService.create(user.id(), request);
    }

    @PutMapping("/crops/{id}")
    public CropResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody CropUpdateRequest request
    ) {
        return cropService.update(user.id(), id, request);
    }

    @DeleteMapping("/crops/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        cropService.delete(user.id(), id);
    }
}