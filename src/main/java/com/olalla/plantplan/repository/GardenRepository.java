package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.Garden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GardenRepository extends JpaRepository<Garden, Long> {

    List<Garden> findByUserId(Long userId);
}
