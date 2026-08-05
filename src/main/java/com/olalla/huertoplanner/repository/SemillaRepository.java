package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Semilla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemillaRepository extends JpaRepository<Semilla, Long> {

    List<Semilla> findByFichaCultivoId(Long fichaCultivoId);

    List<Semilla> findByFichaCultivoUsuarioId(Long usuarioId);
}


