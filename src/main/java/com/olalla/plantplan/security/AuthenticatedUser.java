package com.olalla.plantplan.security;

import com.olalla.plantplan.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record AuthenticatedUser(
        Long id,
        String email,
        Role role
) {

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}