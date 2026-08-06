package com.olalla.plantplan.security;

import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());

            if (jwtService.isValid(token)) {
                authenticate(token);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        Long userId = jwtService.extractUserId(token);

        userRepository.findById(userId)
                .map(this::toPrincipal)
                .map(principal -> new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                ))
                .ifPresent(authentication ->
                        SecurityContextHolder.getContext().setAuthentication(authentication)
                );
    }

    private AuthenticatedUser toPrincipal(User user) {
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole());
    }
}