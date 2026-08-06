package com.olalla.plantplan.dto;

import com.olalla.plantplan.entity.Role;

public record LoginResponse(
        String token,
        UserResponse user
) {
}