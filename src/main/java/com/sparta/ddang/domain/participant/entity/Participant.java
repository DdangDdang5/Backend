package com.sparta.ddang.domain.participant.entity;

import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.util.Timestamped;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Participant extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //    @JoinColumn(name = "member_id" ,nullable = false)
//    @ManyToOne (fetch = FetchType.LAZY)
//    private Member member;
    @Column(name = "member_id", nullable = false)
    private Long memberId;


    // , orphanRemoval=false
    @Column(name = "auction_id", nullable = false)
    private Long auctionId;


//    @JoinColumn (name = "auction_id",nullable = false)
//    @ManyToOne (fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    private Auction auction;

    public Participant() {
    }

    public Participant(Member member, Auction auction) {
        this.memberId = member.getId();
        this.auctionId = auction.getId();
    }

}
