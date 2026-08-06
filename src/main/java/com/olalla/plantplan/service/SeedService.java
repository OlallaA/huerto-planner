package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.SeedCreateRequest;
import com.olalla.plantplan.dto.SeedResponse;
import com.olalla.plantplan.dto.SeedUpdateRequest;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.Seed;
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
    public List<SeedResponse> findAll() {
        return seedRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeedResponse> findByCropSheetId(Long cropSheetId) {
        if (!cropSheetRepository.existsById(cropSheetId)) {
            throw new ResourceNotFoundException("No existe una ficha de cultivo con id " + cropSheetId);
        }

        return seedRepository.findByCropSheetId(cropSheetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeedResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public SeedResponse create(SeedCreateRequest request) {
        CropSheet cropSheet = findCropSheetById(request.cropSheetId());

        Seed seed = new Seed();
        seed.setQuantity(request.quantity());
        seed.setNotes(request.notes());
        seed.setCropSheet(cropSheet);

        Seed saved = seedRepository.save(seed);
        reminderService.generateForSeed(saved);
        return toResponse(saved);
    }

    @Transactional
    public SeedResponse update(Long id, SeedUpdateRequest request) {
        Seed seed = findEntityById(id);
        CropSheet cropSheet = findCropSheetById(request.cropSheetId());

        seed.setQuantity(request.quantity());
        seed.setNotes(request.notes());
        seed.setCropSheet(cropSheet);

        reminderService.generateForSeed(seed);
        return toResponse(seed);
    }

    @Transactional
    public void delete(Long id) {
        Seed seed = findEntityById(id);
        reminderService.deleteForSeed(id);
        seedRepository.delete(seed);
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
