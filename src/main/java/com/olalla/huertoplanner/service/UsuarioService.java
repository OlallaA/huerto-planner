package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.UsuarioCreateRequest;
import com.olalla.huertoplanner.dto.UsuarioResponse;
import com.olalla.huertoplanner.dto.UsuarioUpdateRequest;
import com.olalla.huertoplanner.entity.Usuario;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public UsuarioResponse create(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario(request.nombre(), request.email(), request.password());
        Usuario savedUsuario = usuarioRepository.save(usuario);

        return toResponse(savedUsuario);
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = findEntityById(id);

        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());

        return toResponse(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = findEntityById(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario findEntityById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }
}
