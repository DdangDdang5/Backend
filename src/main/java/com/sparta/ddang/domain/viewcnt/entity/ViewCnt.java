package com.sparta.ddang.domain.viewcnt.entity;

import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.member.entity.Member;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Entity
@DynamicInsert // 디폴트가 null일때 나머지만 insert
public class ViewCnt {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long memberId;

//    @JoinColumn(name = "auction_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)

    @Column(nullable = false)
    private Long auctionId;


    public ViewCnt(Long memId,Long aucId){

        this.memberId = memId;
        this.auctionId = aucId;


    }


}
