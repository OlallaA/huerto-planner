package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.CropSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropSheetRepository extends JpaRepository<CropSheet, Long> {

    List<CropSheet> findByUserId(Long userId);
}

