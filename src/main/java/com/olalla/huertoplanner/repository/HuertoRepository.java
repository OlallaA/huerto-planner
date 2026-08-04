package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Huerto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HuertoRepository extends JpaRepository<Huerto, Long> {

    List<Huerto> findByUsuarioId(Long usuarioId);
}
