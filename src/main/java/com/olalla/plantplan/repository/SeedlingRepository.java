package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.Seedling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeedlingRepository extends JpaRepository<Seedling, Long> {

    List<Seedling> findByCropSheetId(Long cropSheetId);

    List<Seedling> findByCropSheetUserId(Long userId);
}


