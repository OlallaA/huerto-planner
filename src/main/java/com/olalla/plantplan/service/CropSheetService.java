package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.CropSheetCreateRequest;
import com.olalla.plantplan.dto.CropSheetResponse;
import com.olalla.plantplan.dto.CropSheetUpdateRequest;
import com.olalla.plantplan.entity.SunExposure;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.CropSheetRepository;
import com.olalla.plantplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CropSheetService {

    private final CropSheetRepository cropSheetRepository;
    private final UserRepository userRepository;

    public CropSheetService(
            CropSheetRepository cropSheetRepository,
            UserRepository userRepository
    ) {
        this.cropSheetRepository = cropSheetRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CropSheetResponse> findAll() {
        return cropSheetRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CropSheetResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No existe un usuario con id " + userId);
        }

        return cropSheetRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CropSheetResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public CropSheetResponse create(CropSheetCreateRequest request) {
        User user = findUserById(request.userId());

        CropSheet ficha = new CropSheet();
        applyRequest(ficha, request.name(), request.variety(), request.sowingStartMonth(),
                request.sowingEndMonth(), request.transplantStartMonth(), request.transplantEndMonth(),
                request.harvestStartMonth(), request.harvestEndMonth(), request.wateringFrequencyDays(),
                request.sunExposure(), request.notes(), user);

        CropSheet saved = cropSheetRepository.save(ficha);
        return toResponse(saved);
    }

    @Transactional
    public CropSheetResponse update(Long id, CropSheetUpdateRequest request) {
        CropSheet ficha = findEntityById(id);
        User user = findUserById(request.userId());

        applyRequest(ficha, request.name(), request.variety(), request.sowingStartMonth(),
                request.sowingEndMonth(), request.transplantStartMonth(), request.transplantEndMonth(),
                request.harvestStartMonth(), request.harvestEndMonth(), request.wateringFrequencyDays(),
                request.sunExposure(), request.notes(), user);

        return toResponse(ficha);
    }

    @Transactional
    public void delete(Long id) {
        CropSheet ficha = findEntityById(id);
        cropSheetRepository.delete(ficha);
    }

    private void applyRequest(
            CropSheet ficha,
            String name,
            String variety,
            java.time.Month sowingStartMonth,
            java.time.Month sowingEndMonth,
            java.time.Month transplantStartMonth,
            java.time.Month transplantEndMonth,
            java.time.Month harvestStartMonth,
            java.time.Month harvestEndMonth,
            Integer wateringFrequencyDays,
            SunExposure sunExposure,
            String notes,
            User user
    ) {
        ficha.setName(name);
        ficha.setVariety(variety);
        ficha.setSowingStartMonth(sowingStartMonth);
        ficha.setSowingEndMonth(sowingEndMonth);
        ficha.setTransplantStartMonth(transplantStartMonth);
        ficha.setTransplantEndMonth(transplantEndMonth);
        ficha.setHarvestStartMonth(harvestStartMonth);
        ficha.setHarvestEndMonth(harvestEndMonth);
        ficha.setWateringFrequencyDays(wateringFrequencyDays);
        ficha.setSunExposure(sunExposure);
        ficha.setNotes(notes);
        ficha.setUser(user);
    }

    private CropSheet findEntityById(Long id) {
        return cropSheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private CropSheetResponse toResponse(CropSheet ficha) {
        User user = ficha.getUser();

        return new CropSheetResponse(
                ficha.getId(),
                ficha.getName(),
                ficha.getVariety(),
                ficha.getSowingStartMonth(),
                ficha.getSowingEndMonth(),
                ficha.getTransplantStartMonth(),
                ficha.getTransplantEndMonth(),
                ficha.getHarvestStartMonth(),
                ficha.getHarvestEndMonth(),
                ficha.getWateringFrequencyDays(),
                ficha.getSunExposure(),
                ficha.getNotes(),
                user.getId(),
                user.getName()
        );
    }
}
