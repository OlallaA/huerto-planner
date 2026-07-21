package com.olalla.huertoplanner.repository;

import com.olalla.huertoplanner.entity.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
}
