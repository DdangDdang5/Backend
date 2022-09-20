package com.sparta.ddang.domain.search.repository;

import com.sparta.ddang.domain.search.entity.PopularSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopularSearchRepository extends JpaRepository<PopularSearch,Long> {

    boolean existsBySearchWord(String searchWord);

    List<PopularSearch> findAllByOrderBySearchWordCntDesc();

    PopularSearch findBySearchWord(String searchWord);

}
