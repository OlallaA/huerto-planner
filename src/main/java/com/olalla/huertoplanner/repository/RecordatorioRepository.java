package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.EstadoRecordatorio;
import com.olalla.huertoplanner.entity.Recordatorio;
import com.olalla.huertoplanner.entity.TipoRecordatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    List<Recordatorio> findByUsuarioIdAndEstadoInAndFechaProgramadaBetweenOrderByFechaProgramadaAsc(
            Long usuarioId,
            Collection<EstadoRecordatorio> estados,
            LocalDate desde,
            LocalDate hasta
    );

    boolean existsBySemillaIdAndTipoAndEstadoAndFechaProgramada(
            Long semillaId,
            TipoRecordatorio tipo,
            EstadoRecordatorio estado,
            LocalDate fechaProgramada
    );

    boolean existsByPlantelIdAndTipoAndEstadoAndFechaProgramada(
            Long plantelId,
            TipoRecordatorio tipo,
            EstadoRecordatorio estado,
            LocalDate fechaProgramada
    );

    boolean existsByCultivoIdAndTipoAndEstado(
            Long cultivoId,
            TipoRecordatorio tipo,
            EstadoRecordatorio estado
    );

    Optional<Recordatorio> findFirstByCultivoIdAndTipoAndEstadoOrderByFechaProgramadaDesc(
            Long cultivoId,
            TipoRecordatorio tipo,
            EstadoRecordatorio estado
    );

    List<Recordatorio> findBySemillaIdAndTipoInAndEstado(
            Long semillaId,
            Collection<TipoRecordatorio> tipos,
            EstadoRecordatorio estado
    );

    List<Recordatorio> findByPlantelIdAndTipoInAndEstado(
            Long plantelId,
            Collection<TipoRecordatorio> tipos,
            EstadoRecordatorio estado
    );

    List<Recordatorio> findByCultivoIdAndEstado(Long cultivoId, EstadoRecordatorio estado);

    List<Recordatorio> findBySemillaId(Long semillaId);

    List<Recordatorio> findByPlantelId(Long plantelId);

    List<Recordatorio> findByCultivoId(Long cultivoId);
}
