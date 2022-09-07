package com.sparta.ddang.domain.auction.repository;

import com.sparta.ddang.domain.auction.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {

    List<Auction> findAllByOrderByModifiedAtDesc();

    List<Auction> findAllByCategory(String category);
    List<Auction> findAllByRegion(String region);

    List<Auction> findAllByCategoryAndRegion(String category,String region);

    List<Auction> findAllByMember_Id(Long memberId);

    List<Auction> findByTitleContaining(String title);

    Page<Auction> findAllByCategory(String category, Pageable pageable);

    Page<Auction> findAllByRegion(String region, Pageable pageable);

    Page<Auction> findAllByCategoryAndRegion(String category,String region,
                                             Pageable pageable);

    Page<Auction> findAllByMember_Id(Long memberId, Pageable pageable);

}
