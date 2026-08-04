package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.FichaCultivoCreateRequest;
import com.olalla.huertoplanner.dto.FichaCultivoResponse;
import com.olalla.huertoplanner.dto.FichaCultivoUpdateRequest;
import com.olalla.huertoplanner.entity.FichaCultivo;
import com.olalla.huertoplanner.entity.Usuario;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.FichaCultivoRepository;
import com.olalla.huertoplanner.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FichaCultivoService {

    private final FichaCultivoRepository fichaCultivoRepository;
    private final UsuarioRepository usuarioRepository;

    public FichaCultivoService(
            FichaCultivoRepository fichaCultivoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.fichaCultivoRepository = fichaCultivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<FichaCultivoResponse> findAll() {
        return fichaCultivoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FichaCultivoResponse> findByUsuarioId(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("No existe un usuario con id " + usuarioId);
        }

        return fichaCultivoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FichaCultivoResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public FichaCultivoResponse create(FichaCultivoCreateRequest request) {
        Usuario usuario = findUsuarioById(request.usuarioId());

        FichaCultivo ficha = new FichaCultivo();
        applyRequest(ficha, request.nombre(), request.variedad(), request.mesInicioSiembra(),
                request.mesFinSiembra(), request.mesInicioTrasplante(), request.mesFinTrasplante(),
                request.mesInicioCosecha(), request.mesFinCosecha(), request.frecuenciaRiego(),
                request.observaciones(), usuario);

        FichaCultivo saved = fichaCultivoRepository.save(ficha);
        return toResponse(saved);
    }

    @Transactional
    public FichaCultivoResponse update(Long id, FichaCultivoUpdateRequest request) {
        FichaCultivo ficha = findEntityById(id);
        Usuario usuario = findUsuarioById(request.usuarioId());

        applyRequest(ficha, request.nombre(), request.variedad(), request.mesInicioSiembra(),
                request.mesFinSiembra(), request.mesInicioTrasplante(), request.mesFinTrasplante(),
                request.mesInicioCosecha(), request.mesFinCosecha(), request.frecuenciaRiego(),
                request.observaciones(), usuario);

        return toResponse(ficha);
    }

    @Transactional
    public void delete(Long id) {
        FichaCultivo ficha = findEntityById(id);
        fichaCultivoRepository.delete(ficha);
    }

    private void applyRequest(
            FichaCultivo ficha,
            String nombre,
            String variedad,
            java.time.Month mesInicioSiembra,
            java.time.Month mesFinSiembra,
            java.time.Month mesInicioTrasplante,
            java.time.Month mesFinTrasplante,
            java.time.Month mesInicioCosecha,
            java.time.Month mesFinCosecha,
            Integer frecuenciaRiego,
            String observaciones,
            Usuario usuario
    ) {
        ficha.setNombre(nombre);
        ficha.setVariedad(variedad);
        ficha.setMesInicioSiembra(mesInicioSiembra);
        ficha.setMesFinSiembra(mesFinSiembra);
        ficha.setMesInicioTrasplante(mesInicioTrasplante);
        ficha.setMesFinTrasplante(mesFinTrasplante);
        ficha.setMesInicioCosecha(mesInicioCosecha);
        ficha.setMesFinCosecha(mesFinCosecha);
        ficha.setFrecuenciaRiego(frecuenciaRiego);
        ficha.setObservaciones(observaciones);
        ficha.setUsuario(usuario);
    }

    private FichaCultivo findEntityById(Long id) {
        return fichaCultivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id " + id));
    }

    private FichaCultivoResponse toResponse(FichaCultivo ficha) {
        Usuario usuario = ficha.getUsuario();

        return new FichaCultivoResponse(
                ficha.getId(),
                ficha.getNombre(),
                ficha.getVariedad(),
                ficha.getMesInicioSiembra(),
                ficha.getMesFinSiembra(),
                ficha.getMesInicioTrasplante(),
                ficha.getMesFinTrasplante(),
                ficha.getMesInicioCosecha(),
                ficha.getMesFinCosecha(),
                ficha.getFrecuenciaRiego(),
                ficha.getObservaciones(),
                usuario.getId(),
                usuario.getNombre()
        );
    }
}
