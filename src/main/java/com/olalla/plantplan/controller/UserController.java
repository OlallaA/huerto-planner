package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.UserResponse;
import com.olalla.plantplan.dto.UserUpdateRequest;
import com.olalla.plantplan.entity.Role;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user.role() != Role.ADMIN) {
            throw new ForbiddenException("No tienes permiso para listar usuarios");
        }

        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return userService.findById(user.id(), id);
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.update(user.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        userService.delete(user.id(), id);
    }
}