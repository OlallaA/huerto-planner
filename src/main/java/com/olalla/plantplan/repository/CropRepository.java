package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByGardenId(Long gardenId);

    List<Crop> findByCropSheetId(Long cropSheetId);

    List<Crop> findByGardenUserId(Long userId);
}


