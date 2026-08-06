package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.SeedCreateRequest;
import com.olalla.plantplan.dto.SeedResponse;
import com.olalla.plantplan.dto.SeedUpdateRequest;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.Seed;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.CropSheetRepository;
import com.olalla.plantplan.repository.SeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeedService {

    private final SeedRepository seedRepository;
    private final CropSheetRepository cropSheetRepository;
    private final ReminderService reminderService;

    public SeedService(
            SeedRepository seedRepository,
            CropSheetRepository cropSheetRepository,
            ReminderService reminderService
    ) {
        this.seedRepository = seedRepository;
        this.cropSheetRepository = cropSheetRepository;
        this.reminderService = reminderService;
    }

    @Transactional(readOnly = true)
    public List<SeedResponse> findByUserId(Long userId) {
        return seedRepository.findByCropSheetUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeedResponse> findByCropSheetId(Long userId, Long cropSheetId) {
        findOwnedCropSheet(userId, cropSheetId);

        return seedRepository.findByCropSheetId(cropSheetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeedResponse findById(Long userId, Long id) {
        return toResponse(findOwnedEntity(userId, id));
    }

    @Transactional
    public SeedResponse create(Long userId, SeedCreateRequest request) {
        CropSheet cropSheet = findOwnedCropSheet(userId, request.cropSheetId());

        Seed seed = new Seed();
        seed.setQuantity(request.quantity());
        seed.setNotes(request.notes());
        seed.setCropSheet(cropSheet);

        Seed saved = seedRepository.save(seed);
        reminderService.generateForSeed(saved);
        return toResponse(saved);
    }

    @Transactional
    public SeedResponse update(Long userId, Long id, SeedUpdateRequest request) {
        Seed seed = findOwnedEntity(userId, id);
        CropSheet cropSheet = findOwnedCropSheet(userId, request.cropSheetId());

        seed.setQuantity(request.quantity());
        seed.setNotes(request.notes());
        seed.setCropSheet(cropSheet);

        reminderService.generateForSeed(seed);
        return toResponse(seed);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Seed seed = findOwnedEntity(userId, id);
        reminderService.deleteForSeed(id);
        seedRepository.delete(seed);
    }

    private Seed findOwnedEntity(Long userId, Long id) {
        Seed seed = findEntityById(id);

        if (!seed.getCropSheet().getUser().getId().equals(userId)) {
            throw new ForbiddenException("No puedes acceder a la semilla con id " + id);
        }

        return seed;
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

    private Seed findEntityById(Long id) {
        return seedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una semilla con id " + id));
    }

    private CropSheet findCropSheetById(Long id) {
        return cropSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private SeedResponse toResponse(Seed seed) {
        CropSheet cropSheet = seed.getCropSheet();

        return new SeedResponse(
                seed.getId(),
                seed.getQuantity(),
                seed.getNotes(),
                cropSheet.getId(),
                cropSheet.getName()
        );
    }
}