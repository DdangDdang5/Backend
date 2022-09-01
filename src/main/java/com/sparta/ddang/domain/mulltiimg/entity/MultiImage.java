package com.sparta.ddang.domain.mulltiimg.entity;

import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;


@Getter
@Entity
public class MultiImage extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // insertable=false는 insert 시점에 막는 것이고, updatable는 update 시점에 막는 기능
    @Column(name = "imgUrl",updatable = false)
    private String imgUrl;

    @Column(name = "member_id")
    Long memberId;

    @Column(name = "auction_id")
    Long auctionId;

    public MultiImage(){

    }
    // 다중이미지 저장시 사용할 생성자(빌더 패턴)
    @Builder
    public MultiImage(String imgUrl, Long memberId, Long auctionId){

        this.imgUrl = imgUrl;
        this.memberId = memberId;
        this.auctionId = auctionId;

    }




}
