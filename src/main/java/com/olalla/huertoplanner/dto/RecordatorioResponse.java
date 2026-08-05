package com.olalla.huertoplanner.dto;

import com.olalla.huertoplanner.entity.EstadoRecordatorio;
import com.olalla.huertoplanner.entity.TipoRecordatorio;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecordatorioResponse(
        Long id,
        TipoRecordatorio tipo,
        EstadoRecordatorio estado,
        LocalDate fechaProgramada,
        LocalDateTime fechaCompletado,
        String titulo,
        String descripcion,
        Long usuarioId,
        Long fichaCultivoId,
        String fichaCultivoNombre,
        Long semillaId,
        Long plantelId,
        Long cultivoId,
        Long huertoId,
        String huertoNombre
) {
}
