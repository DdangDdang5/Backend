package com.sparta.ddang.domain.search.repository;

import com.sparta.ddang.domain.search.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentSearchRepository extends JpaRepository<RecentSearch,Long> {
    boolean existsByMemberIdAndSearchWord(Long memberId,String searchWord);
    RecentSearch findByMemberIdAndSearchWord(Long memberId,String searchWord);
    List<RecentSearch> findAllByMemberIdOrderByModifiedAtDesc(Long memberId);

}
