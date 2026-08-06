package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.GardenCreateRequest;
import com.olalla.plantplan.dto.GardenResponse;
import com.olalla.plantplan.dto.GardenUpdateRequest;
import com.olalla.plantplan.entity.Garden;
import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.GardenRepository;
import com.olalla.plantplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GardenService {

    private final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    public GardenService(GardenRepository gardenRepository, UserRepository userRepository) {
        this.gardenRepository = gardenRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GardenResponse> findAll() {
        return gardenRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GardenResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No existe un usuario con id " + userId);
        }

        return gardenRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GardenResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public GardenResponse create(GardenCreateRequest request) {
        User user = findUserById(request.userId());

        Garden garden = new Garden();
        garden.setName(request.name());
        garden.setLocation(request.location());
        garden.setLatitude(request.latitude());
        garden.setLongitude(request.longitude());
        garden.setUser(user);

        Garden savedGarden = gardenRepository.save(garden);

        return toResponse(savedGarden);
    }

    @Transactional
    public GardenResponse update(Long id, GardenUpdateRequest request) {
        Garden garden = findEntityById(id);
        User user = findUserById(request.userId());

        garden.setName(request.name());
        garden.setLocation(request.location());
        garden.setLatitude(request.latitude());
        garden.setLongitude(request.longitude());
        garden.setUser(user);

        return toResponse(garden);
    }

    @Transactional
    public void delete(Long id) {
        Garden garden = findEntityById(id);
        gardenRepository.delete(garden);
    }

    private Garden findEntityById(Long id) {
        return gardenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un huerto con id " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private GardenResponse toResponse(Garden garden) {
        User user = garden.getUser();

        return new GardenResponse(
                garden.getId(),
                garden.getName(),
                garden.getLocation(),
                garden.getLatitude(),
                garden.getLongitude(),
                user.getId(),
                user.getName()
        );
    }
}
