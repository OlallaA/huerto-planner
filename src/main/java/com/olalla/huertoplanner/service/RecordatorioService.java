package com.olalla.huertoplanner.service;

import com.olalla.huertoplanner.dto.RecordatorioResponse;
import com.olalla.huertoplanner.entity.Cultivo;
import com.olalla.huertoplanner.entity.EstadoRecordatorio;
import com.olalla.huertoplanner.entity.FichaCultivo;
import com.olalla.huertoplanner.entity.Huerto;
import com.olalla.huertoplanner.entity.Plantel;
import com.olalla.huertoplanner.entity.Recordatorio;
import com.olalla.huertoplanner.entity.Semilla;
import com.olalla.huertoplanner.entity.TipoRecordatorio;
import com.olalla.huertoplanner.entity.Usuario;
import com.olalla.huertoplanner.exception.ResourceNotFoundException;
import com.olalla.huertoplanner.repository.CultivoRepository;
import com.olalla.huertoplanner.repository.PlantelRepository;
import com.olalla.huertoplanner.repository.RecordatorioRepository;
import com.olalla.huertoplanner.repository.SemillaRepository;
import com.olalla.huertoplanner.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class RecordatorioService {

    private static final int DIAS_AVISO_SIEMBRA = 14;
    private static final int DIAS_AVISO_TRASPLANTE = 5;

    private static final Set<TipoRecordatorio> TIPOS_SIEMBRA = EnumSet.of(
            TipoRecordatorio.SIEMBRA_INICIO,
            TipoRecordatorio.SIEMBRA_AVISO_FIN
    );

    private static final Set<TipoRecordatorio> TIPOS_TRASPLANTE = EnumSet.of(
            TipoRecordatorio.TRASPLANTE_INICIO,
            TipoRecordatorio.TRASPLANTE_AVISO_FIN
    );

    private final RecordatorioRepository recordatorioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SemillaRepository semillaRepository;
    private final PlantelRepository plantelRepository;
    private final CultivoRepository cultivoRepository;

    public RecordatorioService(
            RecordatorioRepository recordatorioRepository,
            UsuarioRepository usuarioRepository,
            SemillaRepository semillaRepository,
            PlantelRepository plantelRepository,
            CultivoRepository cultivoRepository
    ) {
        this.recordatorioRepository = recordatorioRepository;
        this.usuarioRepository = usuarioRepository;
        this.semillaRepository = semillaRepository;
        this.plantelRepository = plantelRepository;
        this.cultivoRepository = cultivoRepository;
    }

    @Transactional
    public List<RecordatorioResponse> findByUsuarioAndRango(Long usuarioId, LocalDate desde, LocalDate hasta) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("No existe un usuario con id " + usuarioId);
        }
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas no es valido");
        }

        asegurarRecordatoriosUsuario(usuarioId, desde, hasta);

        return recordatorioRepository
                .findByUsuarioIdAndEstadoInAndFechaProgramadaBetweenOrderByFechaProgramadaAsc(
                        usuarioId,
                        List.of(EstadoRecordatorio.PENDIENTE, EstadoRecordatorio.COMPLETADO),
                        desde,
                        hasta
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecordatorioResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public RecordatorioResponse completar(Long id) {
        Recordatorio recordatorio = findEntityById(id);

        if (recordatorio.getEstado() == EstadoRecordatorio.CANCELADO) {
            throw new IllegalArgumentException("No se puede completar un recordatorio cancelado");
        }
        if (recordatorio.getEstado() == EstadoRecordatorio.COMPLETADO) {
            return toResponse(recordatorio);
        }

        recordatorio.setEstado(EstadoRecordatorio.COMPLETADO);
        recordatorio.setFechaCompletado(LocalDateTime.now());

        if (TIPOS_SIEMBRA.contains(recordatorio.getTipo()) && recordatorio.getSemilla() != null) {
            cancelarPendientesRelacionados(
                    recordatorioRepository.findBySemillaIdAndTipoInAndEstado(
                            recordatorio.getSemilla().getId(),
                            TIPOS_SIEMBRA,
                            EstadoRecordatorio.PENDIENTE
                    ),
                    recordatorio.getId()
            );
        }

        if (TIPOS_TRASPLANTE.contains(recordatorio.getTipo()) && recordatorio.getPlantel() != null) {
            cancelarPendientesRelacionados(
                    recordatorioRepository.findByPlantelIdAndTipoInAndEstado(
                            recordatorio.getPlantel().getId(),
                            TIPOS_TRASPLANTE,
                            EstadoRecordatorio.PENDIENTE
                    ),
                    recordatorio.getId()
            );
        }

        if (recordatorio.getTipo() == TipoRecordatorio.RIEGO && recordatorio.getCultivo() != null) {
            crearSiguienteRiego(recordatorio.getCultivo(), recordatorio.getFechaProgramada());
        }

        return toResponse(recordatorio);
    }

    @Transactional
    public void generarParaSemilla(Semilla semilla) {
        generarSiembraParaSemilla(semilla, LocalDate.now().getYear());
        generarSiembraParaSemilla(semilla, LocalDate.now().getYear() + 1);
    }

    @Transactional
    public void generarParaPlantel(Plantel plantel) {
        if (!plantelPendienteTrasplante(plantel)) {
            cancelarPendientesPlantel(plantel.getId());
            return;
        }
        generarTrasplanteParaPlantel(plantel, LocalDate.now().getYear());
        generarTrasplanteParaPlantel(plantel, LocalDate.now().getYear() + 1);
    }

    @Transactional
    public void generarParaCultivo(Cultivo cultivo) {
        if (cultivo.getFechaFinCultivo() != null && !cultivo.getFechaFinCultivo().isAfter(LocalDate.now())) {
            cancelarPendientesCultivo(cultivo.getId());
            return;
        }
        asegurarProximoRiego(cultivo);
    }

    @Transactional
    public void eliminarParaSemilla(Long semillaId) {
        recordatorioRepository.findBySemillaId(semillaId).forEach(recordatorioRepository::delete);
    }

    @Transactional
    public void eliminarParaPlantel(Long plantelId) {
        recordatorioRepository.findByPlantelId(plantelId).forEach(recordatorioRepository::delete);
    }

    @Transactional
    public void eliminarParaCultivo(Long cultivoId) {
        recordatorioRepository.findByCultivoId(cultivoId).forEach(recordatorioRepository::delete);
    }

    private void asegurarRecordatoriosUsuario(Long usuarioId, LocalDate desde, LocalDate hasta) {
        List<Semilla> semillas = semillaRepository.findByFichaCultivoUsuarioId(usuarioId);
        for (Semilla semilla : semillas) {
            for (int year = desde.getYear(); year <= hasta.getYear() + 1; year++) {
                generarSiembraParaSemilla(semilla, year);
            }
        }

        List<Plantel> planteles = plantelRepository.findByFichaCultivoUsuarioId(usuarioId);
        for (Plantel plantel : planteles) {
            if (!plantelPendienteTrasplante(plantel)) {
                continue;
            }
            for (int year = desde.getYear(); year <= hasta.getYear() + 1; year++) {
                generarTrasplanteParaPlantel(plantel, year);
            }
        }

        List<Cultivo> cultivos = cultivoRepository.findByHuertoUsuarioId(usuarioId);
        for (Cultivo cultivo : cultivos) {
            if (cultivo.getFechaFinCultivo() != null && cultivo.getFechaFinCultivo().isBefore(desde)) {
                continue;
            }
            asegurarProximoRiego(cultivo);
        }
    }

    private void generarSiembraParaSemilla(Semilla semilla, int year) {
        FichaCultivo ficha = semilla.getFichaCultivo();
        Month mesInicio = ficha.getMesInicioSiembra();
        Month mesFin = ficha.getMesFinSiembra();
        if (mesInicio == null || mesFin == null) {
            return;
        }

        Usuario usuario = ficha.getUsuario();
        LocalDate fechaInicio = LocalDate.of(year, mesInicio, 1);
        LocalDate fechaAviso = YearMonth.of(year, mesFin).atEndOfMonth().minusDays(DIAS_AVISO_SIEMBRA);
        String nombre = ficha.getNombre();

        crearSiNoExiste(
                TipoRecordatorio.SIEMBRA_INICIO,
                fechaInicio,
                "Inicio de siembra de " + nombre,
                "Ha comenzado el periodo de siembra de " + nombre + ".",
                usuario,
                ficha,
                semilla,
                null,
                null,
                null
        );

        if (!fechaAviso.isBefore(fechaInicio)) {
            crearSiNoExiste(
                    TipoRecordatorio.SIEMBRA_AVISO_FIN,
                    fechaAviso,
                    "Quedan 2 semanas para sembrar " + nombre,
                    "El periodo de siembra de " + nombre + " termina pronto.",
                    usuario,
                    ficha,
                    semilla,
                    null,
                    null,
                    null
            );
        }
    }

    private void generarTrasplanteParaPlantel(Plantel plantel, int year) {
        FichaCultivo ficha = plantel.getFichaCultivo();
        Month mesInicio = ficha.getMesInicioTrasplante();
        Month mesFin = ficha.getMesFinTrasplante();
        if (mesInicio == null || mesFin == null) {
            return;
        }

        Usuario usuario = ficha.getUsuario();
        LocalDate fechaInicio = LocalDate.of(year, mesInicio, 1);
        LocalDate fechaAviso = YearMonth.of(year, mesFin).atEndOfMonth().minusDays(DIAS_AVISO_TRASPLANTE);
        String nombre = ficha.getNombre();

        crearSiNoExiste(
                TipoRecordatorio.TRASPLANTE_INICIO,
                fechaInicio,
                "Inicio de trasplante de " + nombre,
                "Ha comenzado el periodo de trasplante de " + nombre + ".",
                usuario,
                ficha,
                null,
                plantel,
                null,
                null
        );

        if (!fechaAviso.isBefore(fechaInicio)) {
            crearSiNoExiste(
                    TipoRecordatorio.TRASPLANTE_AVISO_FIN,
                    fechaAviso,
                    "Quedan 5 dias para trasplantar " + nombre,
                    "El periodo de trasplante de " + nombre + " termina pronto.",
                    usuario,
                    ficha,
                    null,
                    plantel,
                    null,
                    null
            );
        }
    }

    private void asegurarProximoRiego(Cultivo cultivo) {
        FichaCultivo ficha = cultivo.getFichaCultivo();
        Integer frecuencia = ficha.getFrecuenciaRiego();
        LocalDate fechaTrasplante = cultivo.getFechaTrasplante();

        if (frecuencia == null || frecuencia <= 0 || fechaTrasplante == null) {
            return;
        }
        if (cultivo.getFechaFinCultivo() != null && !cultivo.getFechaFinCultivo().isAfter(LocalDate.now())) {
            cancelarPendientesCultivo(cultivo.getId());
            return;
        }

        if (recordatorioRepository.existsByCultivoIdAndTipoAndEstado(
                cultivo.getId(),
                TipoRecordatorio.RIEGO,
                EstadoRecordatorio.PENDIENTE
        )) {
            return;
        }

        LocalDate base = recordatorioRepository
                .findFirstByCultivoIdAndTipoAndEstadoOrderByFechaProgramadaDesc(
                        cultivo.getId(),
                        TipoRecordatorio.RIEGO,
                        EstadoRecordatorio.COMPLETADO
                )
                .map(Recordatorio::getFechaProgramada)
                .orElse(fechaTrasplante);

        LocalDate proximo = base.plusDays(frecuencia);
        LocalDate hoy = LocalDate.now();
        while (proximo.isBefore(hoy)) {
            proximo = proximo.plusDays(frecuencia);
        }

        if (cultivo.getFechaFinCultivo() != null && proximo.isAfter(cultivo.getFechaFinCultivo())) {
            return;
        }

        Huerto huerto = cultivo.getHuerto();
        String nombre = ficha.getNombre();
        String huertoNombre = huerto.getNombre();

        Recordatorio riego = new Recordatorio();
        riego.setTipo(TipoRecordatorio.RIEGO);
        riego.setEstado(EstadoRecordatorio.PENDIENTE);
        riego.setFechaProgramada(proximo);
        riego.setTitulo("Regar " + nombre);
        riego.setDescripcion("Proximo riego de " + nombre + " en " + huertoNombre + ".");
        riego.setUsuario(huerto.getUsuario());
        riego.setFichaCultivo(ficha);
        riego.setCultivo(cultivo);
        riego.setHuerto(huerto);
        recordatorioRepository.save(riego);
    }

    private void crearSiguienteRiego(Cultivo cultivo, LocalDate fechaUltimoRiego) {
        FichaCultivo ficha = cultivo.getFichaCultivo();
        Integer frecuencia = ficha.getFrecuenciaRiego();
        if (frecuencia == null || frecuencia <= 0) {
            return;
        }

        LocalDate siguiente = fechaUltimoRiego.plusDays(frecuencia);
        if (cultivo.getFechaFinCultivo() != null && siguiente.isAfter(cultivo.getFechaFinCultivo())) {
            return;
        }

        if (recordatorioRepository.existsByCultivoIdAndTipoAndEstado(
                cultivo.getId(),
                TipoRecordatorio.RIEGO,
                EstadoRecordatorio.PENDIENTE
        )) {
            return;
        }

        Huerto huerto = cultivo.getHuerto();
        String nombre = ficha.getNombre();

        Recordatorio riego = new Recordatorio();
        riego.setTipo(TipoRecordatorio.RIEGO);
        riego.setEstado(EstadoRecordatorio.PENDIENTE);
        riego.setFechaProgramada(siguiente);
        riego.setTitulo("Regar " + nombre);
        riego.setDescripcion("Proximo riego de " + nombre + " en " + huerto.getNombre() + ".");
        riego.setUsuario(huerto.getUsuario());
        riego.setFichaCultivo(ficha);
        riego.setCultivo(cultivo);
        riego.setHuerto(huerto);
        recordatorioRepository.save(riego);
    }

    private void crearSiNoExiste(
            TipoRecordatorio tipo,
            LocalDate fecha,
            String titulo,
            String descripcion,
            Usuario usuario,
            FichaCultivo ficha,
            Semilla semilla,
            Plantel plantel,
            Cultivo cultivo,
            Huerto huerto
    ) {
        if (semilla != null && recordatorioRepository.existsBySemillaIdAndTipoAndEstadoAndFechaProgramada(
                semilla.getId(), tipo, EstadoRecordatorio.PENDIENTE, fecha
        )) {
            return;
        }
        if (semilla != null && recordatorioRepository.existsBySemillaIdAndTipoAndEstadoAndFechaProgramada(
                semilla.getId(), tipo, EstadoRecordatorio.COMPLETADO, fecha
        )) {
            return;
        }
        if (plantel != null && recordatorioRepository.existsByPlantelIdAndTipoAndEstadoAndFechaProgramada(
                plantel.getId(), tipo, EstadoRecordatorio.PENDIENTE, fecha
        )) {
            return;
        }
        if (plantel != null && recordatorioRepository.existsByPlantelIdAndTipoAndEstadoAndFechaProgramada(
                plantel.getId(), tipo, EstadoRecordatorio.COMPLETADO, fecha
        )) {
            return;
        }

        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setTipo(tipo);
        recordatorio.setEstado(EstadoRecordatorio.PENDIENTE);
        recordatorio.setFechaProgramada(fecha);
        recordatorio.setTitulo(titulo);
        recordatorio.setDescripcion(descripcion);
        recordatorio.setUsuario(usuario);
        recordatorio.setFichaCultivo(ficha);
        recordatorio.setSemilla(semilla);
        recordatorio.setPlantel(plantel);
        recordatorio.setCultivo(cultivo);
        recordatorio.setHuerto(huerto);
        recordatorioRepository.save(recordatorio);
    }

    private boolean plantelPendienteTrasplante(Plantel plantel) {
        if (plantel.getFechaTrasplante() != null
                && plantel.getCantidadTrasplantada() != null
                && plantel.getCantidadSembrada() != null
                && plantel.getCantidadTrasplantada() >= plantel.getCantidadSembrada()) {
            return false;
        }
        return plantel.getFechaTrasplante() == null
                || plantel.getCantidadTrasplantada() == null
                || plantel.getCantidadSembrada() == null
                || plantel.getCantidadTrasplantada() < plantel.getCantidadSembrada();
    }

    private void cancelarPendientesRelacionados(List<Recordatorio> pendientes, Long idActual) {
        for (Recordatorio pendiente : pendientes) {
            if (!pendiente.getId().equals(idActual)) {
                pendiente.setEstado(EstadoRecordatorio.CANCELADO);
            }
        }
    }

    private void cancelarPendientesPlantel(Long plantelId) {
        recordatorioRepository.findByPlantelIdAndTipoInAndEstado(
                plantelId,
                TIPOS_TRASPLANTE,
                EstadoRecordatorio.PENDIENTE
        ).forEach(recordatorio -> recordatorio.setEstado(EstadoRecordatorio.CANCELADO));
    }

    private void cancelarPendientesCultivo(Long cultivoId) {
        recordatorioRepository.findByCultivoIdAndEstado(cultivoId, EstadoRecordatorio.PENDIENTE)
                .forEach(recordatorio -> recordatorio.setEstado(EstadoRecordatorio.CANCELADO));
    }

    private Recordatorio findEntityById(Long id) {
        return recordatorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un recordatorio con id " + id));
    }

    private RecordatorioResponse toResponse(Recordatorio recordatorio) {
        FichaCultivo ficha = recordatorio.getFichaCultivo();
        Huerto huerto = recordatorio.getHuerto();
        Semilla semilla = recordatorio.getSemilla();
        Plantel plantel = recordatorio.getPlantel();
        Cultivo cultivo = recordatorio.getCultivo();

        return new RecordatorioResponse(
                recordatorio.getId(),
                recordatorio.getTipo(),
                recordatorio.getEstado(),
                recordatorio.getFechaProgramada(),
                recordatorio.getFechaCompletado(),
                recordatorio.getTitulo(),
                recordatorio.getDescripcion(),
                recordatorio.getUsuario().getId(),
                ficha != null ? ficha.getId() : null,
                ficha != null ? ficha.getNombre() : null,
                semilla != null ? semilla.getId() : null,
                plantel != null ? plantel.getId() : null,
                cultivo != null ? cultivo.getId() : null,
                huerto != null ? huerto.getId() : null,
                huerto != null ? huerto.getNombre() : null
        );
    }
}
