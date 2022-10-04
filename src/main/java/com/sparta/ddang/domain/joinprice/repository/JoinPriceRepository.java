package com.sparta.ddang.domain.joinprice.repository;

import com.sparta.ddang.domain.joinprice.entity.JoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinPriceRepository extends JpaRepository<JoinPrice,Long> {
    List<JoinPrice> findAllByAuctionIdOrderByJoinPriceDesc(Long auctionId);

}
