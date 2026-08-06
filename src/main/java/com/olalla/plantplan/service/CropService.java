package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.CropCreateRequest;
import com.olalla.plantplan.dto.CropResponse;
import com.olalla.plantplan.dto.CropUpdateRequest;
import com.olalla.plantplan.entity.Crop;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.Garden;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.CropRepository;
import com.olalla.plantplan.repository.CropSheetRepository;
import com.olalla.plantplan.repository.GardenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final CropSheetRepository cropSheetRepository;
    private final GardenRepository gardenRepository;
    private final ReminderService reminderService;

    public CropService(
            CropRepository cropRepository,
            CropSheetRepository cropSheetRepository,
            GardenRepository gardenRepository,
            ReminderService reminderService
    ) {
        this.cropRepository = cropRepository;
        this.cropSheetRepository = cropSheetRepository;
        this.gardenRepository = gardenRepository;
        this.reminderService = reminderService;
    }

    @Transactional(readOnly = true)
    public List<CropResponse> findAll() {
        return cropRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CropResponse> findByGardenId(Long gardenId) {
        if (!gardenRepository.existsById(gardenId)) {
            throw new ResourceNotFoundException("No existe un huerto con id " + gardenId);
        }

        return cropRepository.findByGardenId(gardenId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CropResponse> findByCropSheetId(Long cropSheetId) {
        if (!cropSheetRepository.existsById(cropSheetId)) {
            throw new ResourceNotFoundException("No existe una ficha de cultivo con id " + cropSheetId);
        }

        return cropRepository.findByCropSheetId(cropSheetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CropResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public CropResponse create(CropCreateRequest request) {
        CropSheet cropSheet = findCropSheetById(request.cropSheetId());
        Garden garden = findGardenById(request.gardenId());

        Crop crop = new Crop();
        applyRequest(
                crop,
                request.initialQuantity(),
                request.transplantDate(),
                request.harvestedQuantity(),
                request.endDate(),
                request.notes(),
                cropSheet,
                garden
        );

        Crop saved = cropRepository.save(crop);
        reminderService.generateForCrop(saved);
        return toResponse(saved);
    }

    @Transactional
    public CropResponse update(Long id, CropUpdateRequest request) {
        Crop crop = findEntityById(id);
        CropSheet cropSheet = findCropSheetById(request.cropSheetId());
        Garden garden = findGardenById(request.gardenId());

        applyRequest(
                crop,
                request.initialQuantity(),
                request.transplantDate(),
                request.harvestedQuantity(),
                request.endDate(),
                request.notes(),
                cropSheet,
                garden
        );

        reminderService.generateForCrop(crop);
        return toResponse(crop);
    }

    @Transactional
    public void delete(Long id) {
        Crop crop = findEntityById(id);
        reminderService.deleteForCrop(id);
        cropRepository.delete(crop);
    }

    private void applyRequest(
            Crop crop,
            Integer initialQuantity,
            java.time.LocalDate transplantDate,
            Integer harvestedQuantity,
            java.time.LocalDate endDate,
            String notes,
            CropSheet cropSheet,
            Garden garden
    ) {
        crop.setInitialQuantity(initialQuantity);
        crop.setTransplantDate(transplantDate);
        crop.setHarvestedQuantity(harvestedQuantity);
        crop.setEndDate(endDate);
        crop.setNotes(notes);
        crop.setCropSheet(cropSheet);
        crop.setGarden(garden);
    }

    private Crop findEntityById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un cultivo con id " + id));
    }

    private CropSheet findCropSheetById(Long id) {
        return cropSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private Garden findGardenById(Long id) {
        return gardenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un huerto con id " + id));
    }

    private CropResponse toResponse(Crop crop) {
        CropSheet cropSheet = crop.getCropSheet();
        Garden garden = crop.getGarden();

        return new CropResponse(
                crop.getId(),
                crop.getInitialQuantity(),
                crop.getTransplantDate(),
                crop.getHarvestedQuantity(),
                crop.getEndDate(),
                crop.getNotes(),
                cropSheet.getId(),
                cropSheet.getName(),
                garden.getId(),
                garden.getName()
        );
    }
}
