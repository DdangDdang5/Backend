package com.sparta.ddang.domain.tag.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.ddang.domain.auction.dto.request.AuctionTagsRequestDto;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
public class Tags {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    @ColumnDefault("0")
    String tag1;

    @Column
    @ColumnDefault("0")
    String tag2;

    @Column
    @ColumnDefault("0")
    String tag3;

    @Column
    @ColumnDefault("0")
    String tag4;

    @Column
    @ColumnDefault("0")
    String tag5;

    @Column
    @ColumnDefault("0")
    String tag6;
    
    @JsonIgnore
    @Column(name = "member_id")
    Long memberId;

    @Column(name = "auction_id")
    Long auctionId;

    public Tags(){

    }

    @Builder
    public Tags(Long memberId, Long auctionId,AuctionTagsRequestDto auctionTagsRequestDto
                ){

        this.tag1 = auctionTagsRequestDto.getTag1();
        this.tag2 = auctionTagsRequestDto.getTag2();
        this.tag3 = auctionTagsRequestDto.getTag3();
        this.tag4 = auctionTagsRequestDto.getTag4();
        this.tag5 = auctionTagsRequestDto.getTag5();
        this.tag6 = auctionTagsRequestDto.getTag6();
        this.memberId =memberId;
        this.auctionId = auctionId;


    }

    public void updateTags(AuctionTagsRequestDto auctionTagsRequestDto){

        this.tag1 = auctionTagsRequestDto.getTag1();
        this.tag2 = auctionTagsRequestDto.getTag2();
        this.tag3 = auctionTagsRequestDto.getTag3();
        this.tag4 = auctionTagsRequestDto.getTag4();
        this.tag5 = auctionTagsRequestDto.getTag5();
        this.tag6 = auctionTagsRequestDto.getTag6();

    }

    public void addAuctionId(Long auctionId) {

        this.auctionId = auctionId;

    }
}
