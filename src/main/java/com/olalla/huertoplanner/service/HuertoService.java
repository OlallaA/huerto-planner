package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.HuertoCreateRequest;
import com.olalla.huertoplanner.dto.HuertoResponse;
import com.olalla.huertoplanner.dto.HuertoUpdateRequest;
import com.olalla.huertoplanner.entity.Huerto;
import com.olalla.huertoplanner.entity.Usuario;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.HuertoRepository;
import com.olalla.huertoplanner.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HuertoService {

    private final HuertoRepository huertoRepository;
    private final UsuarioRepository usuarioRepository;

    public HuertoService(HuertoRepository huertoRepository, UsuarioRepository usuarioRepository) {
        this.huertoRepository = huertoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<HuertoResponse> findAll() {
        return huertoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HuertoResponse> findByUsuarioId(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("No existe un usuario con id " + usuarioId);
        }

        return huertoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HuertoResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public HuertoResponse create(HuertoCreateRequest request) {
        Usuario usuario = findUsuarioById(request.usuarioId());

        Huerto huerto = new Huerto();
        huerto.setNombre(request.nombre());
        huerto.setUbicacion(request.ubicacion());
        huerto.setLatitud(request.latitud());
        huerto.setLongitud(request.longitud());
        huerto.setUsuario(usuario);

        Huerto savedHuerto = huertoRepository.save(huerto);

        return toResponse(savedHuerto);
    }

    @Transactional
    public HuertoResponse update(Long id, HuertoUpdateRequest request) {
        Huerto huerto = findEntityById(id);
        Usuario usuario = findUsuarioById(request.usuarioId());

        huerto.setNombre(request.nombre());
        huerto.setUbicacion(request.ubicacion());
        huerto.setLatitud(request.latitud());
        huerto.setLongitud(request.longitud());
        huerto.setUsuario(usuario);

        return toResponse(huerto);
    }

    @Transactional
    public void delete(Long id) {
        Huerto huerto = findEntityById(id);
        huertoRepository.delete(huerto);
    }

    private Huerto findEntityById(Long id) {
        return huertoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un huerto con id " + id));
    }

    private Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private HuertoResponse toResponse(Huerto huerto) {
        Usuario usuario = huerto.getUsuario();

        return new HuertoResponse(
                huerto.getId(),
                huerto.getNombre(),
                huerto.getUbicacion(),
                huerto.getLatitud(),
                huerto.getLongitud(),
                usuario.getId(),
                usuario.getNombre()
        );
    }
}
