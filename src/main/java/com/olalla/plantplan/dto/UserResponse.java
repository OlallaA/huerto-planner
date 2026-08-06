package com.olalla.plantplan.dto;

import com.olalla.plantplan.entity.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
