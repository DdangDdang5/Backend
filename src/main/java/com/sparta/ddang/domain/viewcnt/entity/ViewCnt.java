package com.sparta.ddang.domain.viewcnt.entity;

import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Entity
@DynamicInsert
public class ViewCnt {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long auctionId;

    public ViewCnt(Long memId,Long aucId){
        this.memberId = memId;
        this.auctionId = aucId;
    }

    public ViewCnt() {}

}
