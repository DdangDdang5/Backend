package com.sparta.ddang.domain.viewcnt.repository;

import com.sparta.ddang.domain.viewcnt.entity.ViewCnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCntRepository extends JpaRepository<ViewCnt,Long> {

    boolean existsByMemberIdAndAuctionId(Long memberId,Long auctionId);
}
