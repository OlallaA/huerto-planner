package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.Seed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeedRepository extends JpaRepository<Seed, Long> {

    List<Seed> findByCropSheetId(Long cropSheetId);

    List<Seed> findByCropSheetUserId(Long userId);
}


