package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.CultivoCreateRequest;
import com.olalla.huertoplanner.dto.CultivoResponse;
import com.olalla.huertoplanner.dto.CultivoUpdateRequest;
import com.olalla.huertoplanner.entity.Cultivo;
import com.olalla.huertoplanner.entity.FichaCultivo;
import com.olalla.huertoplanner.entity.Huerto;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.CultivoRepository;
import com.olalla.huertoplanner.repository.FichaCultivoRepository;
import com.olalla.huertoplanner.repository.HuertoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CultivoService {

    private final CultivoRepository cultivoRepository;
    private final FichaCultivoRepository fichaCultivoRepository;
    private final HuertoRepository huertoRepository;
    private final RecordatorioService recordatorioService;

    public CultivoService(
            CultivoRepository cultivoRepository,
            FichaCultivoRepository fichaCultivoRepository,
            HuertoRepository huertoRepository,
            RecordatorioService recordatorioService
    ) {
        this.cultivoRepository = cultivoRepository;
        this.fichaCultivoRepository = fichaCultivoRepository;
        this.huertoRepository = huertoRepository;
        this.recordatorioService = recordatorioService;
    }

    @Transactional(readOnly = true)
    public List<CultivoResponse> findAll() {
        return cultivoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CultivoResponse> findByHuertoId(Long huertoId) {
        if (!huertoRepository.existsById(huertoId)) {
            throw new ResourceNotFoundException("No existe un huerto con id " + huertoId);
        }

        return cultivoRepository.findByHuertoId(huertoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CultivoResponse> findByFichaCultivoId(Long fichaCultivoId) {
        if (!fichaCultivoRepository.existsById(fichaCultivoId)) {
            throw new ResourceNotFoundException("No existe una ficha de cultivo con id " + fichaCultivoId);
        }

        return cultivoRepository.findByFichaCultivoId(fichaCultivoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CultivoResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public CultivoResponse create(CultivoCreateRequest request) {
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());
        Huerto huerto = findHuertoById(request.huertoId());

        Cultivo cultivo = new Cultivo();
        applyRequest(
                cultivo,
                request.cantidadInicial(),
                request.fechaTrasplante(),
                request.cantidadCosechada(),
                request.fechaFinCultivo(),
                request.observaciones(),
                fichaCultivo,
                huerto
        );

        Cultivo saved = cultivoRepository.save(cultivo);
        recordatorioService.generarParaCultivo(saved);
        return toResponse(saved);
    }

    @Transactional
    public CultivoResponse update(Long id, CultivoUpdateRequest request) {
        Cultivo cultivo = findEntityById(id);
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());
        Huerto huerto = findHuertoById(request.huertoId());

        applyRequest(
                cultivo,
                request.cantidadInicial(),
                request.fechaTrasplante(),
                request.cantidadCosechada(),
                request.fechaFinCultivo(),
                request.observaciones(),
                fichaCultivo,
                huerto
        );

        recordatorioService.generarParaCultivo(cultivo);
        return toResponse(cultivo);
    }

    @Transactional
    public void delete(Long id) {
        Cultivo cultivo = findEntityById(id);
        recordatorioService.eliminarParaCultivo(id);
        cultivoRepository.delete(cultivo);
    }

    private void applyRequest(
            Cultivo cultivo,
            Integer cantidadInicial,
            java.time.LocalDate fechaTrasplante,
            Integer cantidadCosechada,
            java.time.LocalDate fechaFinCultivo,
            String observaciones,
            FichaCultivo fichaCultivo,
            Huerto huerto
    ) {
        cultivo.setCantidadInicial(cantidadInicial);
        cultivo.setFechaTrasplante(fechaTrasplante);
        cultivo.setCantidadCosechada(cantidadCosechada);
        cultivo.setFechaFinCultivo(fechaFinCultivo);
        cultivo.setObservaciones(observaciones);
        cultivo.setFichaCultivo(fichaCultivo);
        cultivo.setHuerto(huerto);
    }

    private Cultivo findEntityById(Long id) {
        return cultivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un cultivo con id " + id));
    }

    private FichaCultivo findFichaCultivoById(Long id) {
        return fichaCultivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private Huerto findHuertoById(Long id) {
        return huertoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un huerto con id " + id));
    }

    private CultivoResponse toResponse(Cultivo cultivo) {
        FichaCultivo fichaCultivo = cultivo.getFichaCultivo();
        Huerto huerto = cultivo.getHuerto();

        return new CultivoResponse(
                cultivo.getId(),
                cultivo.getCantidadInicial(),
                cultivo.getFechaTrasplante(),
                cultivo.getCantidadCosechada(),
                cultivo.getFechaFinCultivo(),
                cultivo.getObservaciones(),
                fichaCultivo.getId(),
                fichaCultivo.getNombre(),
                huerto.getId(),
                huerto.getNombre()
        );
    }
}
