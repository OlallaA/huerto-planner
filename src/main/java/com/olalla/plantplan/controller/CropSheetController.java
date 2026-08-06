package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.CropSheetCreateRequest;
import com.olalla.plantplan.dto.CropSheetResponse;
import com.olalla.plantplan.dto.CropSheetUpdateRequest;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.CropSheetService;
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
public class CropSheetController {

    private final CropSheetService cropSheetService;

    public CropSheetController(CropSheetService cropSheetService) {
        this.cropSheetService = cropSheetService;
    }

    @GetMapping("/crop-sheets")
    public List<CropSheetResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return cropSheetService.findByUserId(user.id());
    }

    @GetMapping("/users/{userId}/crop-sheets")
    public List<CropSheetResponse> findByUserId(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long userId
    ) {
        if (!user.id().equals(userId)) {
            throw new ForbiddenException("No puedes acceder a las fichas de cultivo de otro usuario");
        }

        return cropSheetService.findByUserId(userId);
    }

    @GetMapping("/crop-sheets/{id}")
    public CropSheetResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return cropSheetService.findById(user.id(), id);
    }

    @PostMapping("/crop-sheets")
    @ResponseStatus(HttpStatus.CREATED)
    public CropSheetResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CropSheetCreateRequest request
    ) {
        return cropSheetService.create(user.id(), request);
    }

    @PutMapping("/crop-sheets/{id}")
    public CropSheetResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody CropSheetUpdateRequest request
    ) {
        return cropSheetService.update(user.id(), id, request);
    }

    @DeleteMapping("/crop-sheets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        cropSheetService.delete(user.id(), id);
    }
}