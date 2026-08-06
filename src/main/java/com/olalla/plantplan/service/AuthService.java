package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.LoginRequest;
import com.olalla.plantplan.dto.LoginResponse;
import com.olalla.plantplan.dto.RegisterRequest;
import com.olalla.plantplan.dto.UserResponse;
import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.repository.UserRepository;
import com.olalla.plantplan.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        User user = new User(request.name(), request.email(), passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());

        return new LoginResponse(token, toResponse(user));
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