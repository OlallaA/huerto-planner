package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.SeedlingCreateRequest;
import com.olalla.plantplan.dto.SeedlingResponse;
import com.olalla.plantplan.dto.SeedlingUpdateRequest;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.Seedling;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.CropSheetRepository;
import com.olalla.plantplan.repository.SeedlingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeedlingService {

    private final SeedlingRepository seedlingRepository;
    private final CropSheetRepository cropSheetRepository;
    private final ReminderService reminderService;

    public SeedlingService(
            SeedlingRepository seedlingRepository,
            CropSheetRepository cropSheetRepository,
            ReminderService reminderService
    ) {
        this.seedlingRepository = seedlingRepository;
        this.cropSheetRepository = cropSheetRepository;
        this.reminderService = reminderService;
    }

    @Transactional(readOnly = true)
    public List<SeedlingResponse> findByUserId(Long userId) {
        return seedlingRepository.findByCropSheetUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeedlingResponse> findByCropSheetId(Long userId, Long cropSheetId) {
        findOwnedCropSheet(userId, cropSheetId);

        return seedlingRepository.findByCropSheetId(cropSheetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeedlingResponse findById(Long userId, Long id) {
        return toResponse(findOwnedEntity(userId, id));
    }

    @Transactional
    public SeedlingResponse create(Long userId, SeedlingCreateRequest request) {
        CropSheet cropSheet = findOwnedCropSheet(userId, request.cropSheetId());

        Seedling seedling = new Seedling();
        applyRequest(
                seedling,
                request.sownQuantity(),
                request.sowingDate(),
                request.transplantedQuantity(),
                request.transplantDate(),
                request.notes(),
                cropSheet
        );

        Seedling saved = seedlingRepository.save(seedling);
        reminderService.generateForSeedling(saved);
        return toResponse(saved);
    }

    @Transactional
    public SeedlingResponse update(Long userId, Long id, SeedlingUpdateRequest request) {
        Seedling seedling = findOwnedEntity(userId, id);
        CropSheet cropSheet = findOwnedCropSheet(userId, request.cropSheetId());

        applyRequest(
                seedling,
                request.sownQuantity(),
                request.sowingDate(),
                request.transplantedQuantity(),
                request.transplantDate(),
                request.notes(),
                cropSheet
        );

        reminderService.generateForSeedling(seedling);
        return toResponse(seedling);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Seedling seedling = findOwnedEntity(userId, id);
        reminderService.deleteForSeedling(id);
        seedlingRepository.delete(seedling);
    }

    private void applyRequest(
            Seedling seedling,
            Integer sownQuantity,
            java.time.LocalDate sowingDate,
            Integer transplantedQuantity,
            java.time.LocalDate transplantDate,
            String notes,
            CropSheet cropSheet
    ) {
        seedling.setSownQuantity(sownQuantity);
        seedling.setSowingDate(sowingDate);
        seedling.setTransplantedQuantity(transplantedQuantity);
        seedling.setTransplantDate(transplantDate);
        seedling.setNotes(notes);
        seedling.setCropSheet(cropSheet);
    }

    private Seedling findOwnedEntity(Long userId, Long id) {
        Seedling seedling = findEntityById(id);

        if (!seedling.getCropSheet().getUser().getId().equals(userId)) {
            throw new ForbiddenException("No puedes acceder al plantel con id " + id);
        }

        return seedling;
    }

    private CropSheet findOwnedCropSheet(Long userId, Long cropSheetId) {
        CropSheet cropSheet = findCropSheetById(cropSheetId);

        if (!cropSheet.getUser().getId().equals(userId)) {
            throw new ForbiddenException(
                    "No puedes acceder a la ficha de cultivo con id " + cropSheetId
            );
        }

        return cropSheet;
    }

    private Seedling findEntityById(Long id) {
        return seedlingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un plantel con id " + id));
    }

    private CropSheet findCropSheetById(Long id) {
        return cropSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private SeedlingResponse toResponse(Seedling seedling) {
        CropSheet cropSheet = seedling.getCropSheet();

        return new SeedlingResponse(
                seedling.getId(),
                seedling.getSownQuantity(),
                seedling.getSowingDate(),
                seedling.getTransplantedQuantity(),
                seedling.getTransplantDate(),
                seedling.getNotes(),
                cropSheet.getId(),
                cropSheet.getName()
        );
    }
}