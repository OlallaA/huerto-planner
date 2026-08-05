package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Plantel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantelRepository extends JpaRepository<Plantel, Long> {

    List<Plantel> findByFichaCultivoId(Long fichaCultivoId);

    List<Plantel> findByFichaCultivoUsuarioId(Long usuarioId);
}


