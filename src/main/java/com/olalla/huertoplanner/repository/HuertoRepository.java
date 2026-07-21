package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Huerto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HuertoRepository extends JpaRepository<Huerto, Long> {
}
