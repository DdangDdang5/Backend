package com.sparta.ddang.domain.region.repository;

import com.sparta.ddang.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region,Long> {

    boolean existsByRegion(String region);

    Optional<Region> findByRegion(String regi);

    void deleteByRegion(String region);

    List<Region> findAllByOrderByViewerCntDesc();

    List<Region> findAllByOrderByRegionAsc();


    //Long countAllByRegion();
    //Long countAll();


}
