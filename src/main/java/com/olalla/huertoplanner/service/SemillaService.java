package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.SemillaCreateRequest;
import com.olalla.huertoplanner.dto.SemillaResponse;
import com.olalla.huertoplanner.dto.SemillaUpdateRequest;
import com.olalla.huertoplanner.entity.FichaCultivo;
import com.olalla.huertoplanner.entity.Semilla;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.FichaCultivoRepository;
import com.olalla.huertoplanner.repository.SemillaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemillaService {

    private final SemillaRepository semillaRepository;
    private final FichaCultivoRepository fichaCultivoRepository;
    private final RecordatorioService recordatorioService;

    public SemillaService(
            SemillaRepository semillaRepository,
            FichaCultivoRepository fichaCultivoRepository,
            RecordatorioService recordatorioService
    ) {
        this.semillaRepository = semillaRepository;
        this.fichaCultivoRepository = fichaCultivoRepository;
        this.recordatorioService = recordatorioService;
    }

    @Transactional(readOnly = true)
    public List<SemillaResponse> findAll() {
        return semillaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SemillaResponse> findByFichaCultivoId(Long fichaCultivoId) {
        if (!fichaCultivoRepository.existsById(fichaCultivoId)) {
            throw new ResourceNotFoundException("No existe una ficha de cultivo con id " + fichaCultivoId);
        }

        return semillaRepository.findByFichaCultivoId(fichaCultivoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SemillaResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public SemillaResponse create(SemillaCreateRequest request) {
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());

        Semilla semilla = new Semilla();
        semilla.setCantidad(request.cantidad());
        semilla.setObservaciones(request.observaciones());
        semilla.setFichaCultivo(fichaCultivo);

        Semilla saved = semillaRepository.save(semilla);
        recordatorioService.generarParaSemilla(saved);
        return toResponse(saved);
    }

    @Transactional
    public SemillaResponse update(Long id, SemillaUpdateRequest request) {
        Semilla semilla = findEntityById(id);
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());

        semilla.setCantidad(request.cantidad());
        semilla.setObservaciones(request.observaciones());
        semilla.setFichaCultivo(fichaCultivo);

        recordatorioService.generarParaSemilla(semilla);
        return toResponse(semilla);
    }

    @Transactional
    public void delete(Long id) {
        Semilla semilla = findEntityById(id);
        recordatorioService.eliminarParaSemilla(id);
        semillaRepository.delete(semilla);
    }

    private Semilla findEntityById(Long id) {
        return semillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una semilla con id " + id));
    }

    private FichaCultivo findFichaCultivoById(Long id) {
        return fichaCultivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private SemillaResponse toResponse(Semilla semilla) {
        FichaCultivo fichaCultivo = semilla.getFichaCultivo();

        return new SemillaResponse(
                semilla.getId(),
                semilla.getCantidad(),
                semilla.getObservaciones(),
                fichaCultivo.getId(),
                fichaCultivo.getNombre()
        );
    }
}
