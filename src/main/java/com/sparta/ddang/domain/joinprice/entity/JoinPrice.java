package com.sparta.ddang.domain.joinprice.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class JoinPrice {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "member_id" ,nullable = false)
    private Long memberId;

    @Column (name = "auction_id",nullable = false)
    private Long auctionId;

    @Column (name = "join_price",nullable = false)
    private Long joinPrice;

    public JoinPrice(){}

    @Builder
    public JoinPrice(Long memberId, Long auctionId, Long joinPrice){
        this.memberId = memberId;
        this.auctionId = auctionId;
        this.joinPrice = joinPrice;
    }

}
