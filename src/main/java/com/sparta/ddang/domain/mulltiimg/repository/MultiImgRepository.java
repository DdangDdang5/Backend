package com.sparta.ddang.domain.mulltiimg.repository;

import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultiImgRepository extends JpaRepository<MultiImage, Long> {
    void deleteAllByMemberIdAndAuctionId(Long memberId, Long AuctionId);

}
