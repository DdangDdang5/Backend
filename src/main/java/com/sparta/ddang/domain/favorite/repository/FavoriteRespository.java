package com.sparta.ddang.domain.favorite.repository;

import com.sparta.ddang.domain.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRespository extends JpaRepository<Favorite,Long> {
    Long countAllByMemberId(Long memberId);
    Long countAllByAuctionId(Long auctionId);
    boolean existsByMemberIdAndAuctionId(Long memberId,Long auctionId);
    void deleteByMemberIdAndAuctionId(Long memberId,Long auctionId);
    List<Favorite> findAllByMember_Id(Long memberId);
    Page<Favorite> findAllByMember_Id(Long memberId, Pageable pageable);

}
