package com.sparta.ddang.domain.tag.repository;

import com.sparta.ddang.domain.tag.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    Optional<Tags> findByAuctionId(Long auctionId);
    void deleteByAuctionId(Long auctionId);

}
