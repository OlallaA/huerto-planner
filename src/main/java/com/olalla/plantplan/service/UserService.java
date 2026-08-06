package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.UserCreateRequest;
import com.olalla.plantplan.dto.UserResponse;
import com.olalla.plantplan.dto.UserUpdateRequest;
import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        User user = new User(request.name(), request.email(), request.password());
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findEntityById(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findEntityById(id);
        userRepository.delete(user);
    }

    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
