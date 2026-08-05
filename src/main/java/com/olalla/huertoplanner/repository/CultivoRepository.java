package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {

    List<Cultivo> findByHuertoId(Long huertoId);

    List<Cultivo> findByFichaCultivoId(Long fichaCultivoId);

    List<Cultivo> findByHuertoUsuarioId(Long usuarioId);
}


