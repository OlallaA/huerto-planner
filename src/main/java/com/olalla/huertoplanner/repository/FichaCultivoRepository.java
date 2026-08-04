package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.FichaCultivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FichaCultivoRepository extends JpaRepository<FichaCultivo, Long> {

    List<FichaCultivo> findByUsuarioId(Long usuarioId);
}

