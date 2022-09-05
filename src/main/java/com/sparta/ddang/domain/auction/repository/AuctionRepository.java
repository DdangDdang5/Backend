package com.sparta.ddang.domain.auction.repository;

import com.sparta.ddang.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {

    List<Auction> findAllByOrderByModifiedAtDesc();

    List<Auction> findAllByCategory(String category);
    List<Auction> findAllByRegion(String region);

    List<Auction> findAllByCategoryAndRegion(String category,String region);

    List<Auction> findAllByMember_Id(Long memberId);

}
