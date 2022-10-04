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

    @Column(name = "imgUrl",updatable = false)
    private String imgUrl;

    @Column(name = "member_id")
    Long memberId;

    @Column(name = "auction_id")
    Long auctionId;

    public MultiImage(){}

    @Builder
    public MultiImage(String imgUrl, Long memberId, Long auctionId){
        this.imgUrl = imgUrl;
        this.memberId = memberId;
        this.auctionId = auctionId;
    }

}
