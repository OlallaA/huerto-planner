package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.PlantelCreateRequest;
import com.olalla.huertoplanner.dto.PlantelResponse;
import com.olalla.huertoplanner.dto.PlantelUpdateRequest;
import com.olalla.huertoplanner.entity.FichaCultivo;
import com.olalla.huertoplanner.entity.Plantel;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.FichaCultivoRepository;
import com.olalla.huertoplanner.repository.PlantelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlantelService {

    private final PlantelRepository plantelRepository;
    private final FichaCultivoRepository fichaCultivoRepository;
    private final RecordatorioService recordatorioService;

    public PlantelService(
            PlantelRepository plantelRepository,
            FichaCultivoRepository fichaCultivoRepository,
            RecordatorioService recordatorioService
    ) {
        this.plantelRepository = plantelRepository;
        this.fichaCultivoRepository = fichaCultivoRepository;
        this.recordatorioService = recordatorioService;
    }

    @Transactional(readOnly = true)
    public List<PlantelResponse> findAll() {
        return plantelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlantelResponse> findByFichaCultivoId(Long fichaCultivoId) {
        if (!fichaCultivoRepository.existsById(fichaCultivoId)) {
            throw new ResourceNotFoundException("No existe una ficha de cultivo con id " + fichaCultivoId);
        }

        return plantelRepository.findByFichaCultivoId(fichaCultivoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlantelResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public PlantelResponse create(PlantelCreateRequest request) {
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());

        Plantel plantel = new Plantel();
        applyRequest(
                plantel,
                request.cantidadSembrada(),
                request.fechaSiembra(),
                request.cantidadTrasplantada(),
                request.fechaTrasplante(),
                request.observaciones(),
                fichaCultivo
        );

        Plantel saved = plantelRepository.save(plantel);
        recordatorioService.generarParaPlantel(saved);
        return toResponse(saved);
    }

    @Transactional
    public PlantelResponse update(Long id, PlantelUpdateRequest request) {
        Plantel plantel = findEntityById(id);
        FichaCultivo fichaCultivo = findFichaCultivoById(request.fichaCultivoId());

        applyRequest(
                plantel,
                request.cantidadSembrada(),
                request.fechaSiembra(),
                request.cantidadTrasplantada(),
                request.fechaTrasplante(),
                request.observaciones(),
                fichaCultivo
        );

        recordatorioService.generarParaPlantel(plantel);
        return toResponse(plantel);
    }

    @Transactional
    public void delete(Long id) {
        Plantel plantel = findEntityById(id);
        recordatorioService.eliminarParaPlantel(id);
        plantelRepository.delete(plantel);
    }

    private void applyRequest(
            Plantel plantel,
            Integer cantidadSembrada,
            java.time.LocalDate fechaSiembra,
            Integer cantidadTrasplantada,
            java.time.LocalDate fechaTrasplante,
            String observaciones,
            FichaCultivo fichaCultivo
    ) {
        plantel.setCantidadSembrada(cantidadSembrada);
        plantel.setFechaSiembra(fechaSiembra);
        plantel.setCantidadTrasplantada(cantidadTrasplantada);
        plantel.setFechaTrasplante(fechaTrasplante);
        plantel.setObservaciones(observaciones);
        plantel.setFichaCultivo(fichaCultivo);
    }

    private Plantel findEntityById(Long id) {
        return plantelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un plantel con id " + id));
    }

    private FichaCultivo findFichaCultivoById(Long id) {
        return fichaCultivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una ficha de cultivo con id " + id));
    }

    private PlantelResponse toResponse(Plantel plantel) {
        FichaCultivo fichaCultivo = plantel.getFichaCultivo();

        return new PlantelResponse(
                plantel.getId(),
                plantel.getCantidadSembrada(),
                plantel.getFechaSiembra(),
                plantel.getCantidadTrasplantada(),
                plantel.getFechaTrasplante(),
                plantel.getObservaciones(),
                fichaCultivo.getId(),
                fichaCultivo.getNombre()
        );
    }
}
