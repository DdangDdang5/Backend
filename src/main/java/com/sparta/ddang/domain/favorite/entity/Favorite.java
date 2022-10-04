package com.sparta.ddang.domain.favorite.entity;

import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.util.Timestamped;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Favorite extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "member_id" ,nullable = false)
    @ManyToOne (fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn (name = "auction_id",nullable = false)
    @ManyToOne (fetch = FetchType.LAZY)
    Auction auction;

    public Favorite(){}

    public Favorite(Member member, Auction auction){
        this.member = member;
        this.auction = auction;
    }

}
