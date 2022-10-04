package com.sparta.ddang.domain.auction.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.ddang.domain.auction.dto.request.AuctionRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionUpdateRequestDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.tag.entity.Tags;
import com.sparta.ddang.util.Timestamped;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@DynamicInsert
public class Auction extends Timestamped {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column
    private String productName;

    @Column(nullable = false)
    private String content;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MultiImage> multiImages = new ArrayList<MultiImage>();

    @JoinColumn(name = "tags_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Tags tags;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long startPrice;

    @Column
    @ColumnDefault("0")
    private Long nowPrice;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long auctionPeriod;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String region;

    @Column
    private boolean direct;

    @Column
    private boolean delivery;

    @Column
    @ColumnDefault("0")
    private Long viewerCnt = 0L;

    @Column
    @ColumnDefault("0") //default 0
    private Long participantCnt = 0L;

    @Column
    private boolean auctionStatus; // true

    @Column
    private boolean participantStatus;

    @Column
    private boolean sellerDone;

    @Column
    private boolean bidderDone;

    @Column
    private boolean reviewDone;

    @Column
    private String chatRoomId;

    @Column
    private String bidRoomId;

    @Column
    private String onoRoomId;

    public Auction() {}

    public Auction(List<MultiImage> multiImages,Member member,AuctionRequestDto auctionRequestDto){
        LocalDateTime now = LocalDateTime.now();

        this.member = member;
        this.title = auctionRequestDto.getTitle();
        this.productName = auctionRequestDto.getProductName();
        this.multiImages = multiImages;
        this.content = auctionRequestDto.getContent();
        this.startPrice = auctionRequestDto.getStartPrice();
        this.nowPrice = auctionRequestDto.getStartPrice();
        this.auctionPeriod = auctionRequestDto.getAuctionPeriod();
        this.deadline = calcDeadLine(now, auctionRequestDto.getAuctionPeriod());
        this.category = auctionRequestDto.getCategory();
        this.region = auctionRequestDto.getRegion();
        this.direct = auctionRequestDto.isDirect();
        this.delivery = auctionRequestDto.isDelivery();
        this.auctionStatus = true;
        this.sellerDone = false;
        this.bidderDone = false;
        this.reviewDone = false;
    }

    public Auction(List<MultiImage> multiImages){
        this.multiImages = multiImages;
    }

    public void updateAuction(List<MultiImage> multiImages,Member member,AuctionUpdateRequestDto auctionUpdateRequestDto) {
        this.member = member;
        this.multiImages = multiImages;
        this.title = auctionUpdateRequestDto.getTitle();
        this.productName = auctionUpdateRequestDto.getProductName();
        this.content = auctionUpdateRequestDto.getContent();
        this.region = auctionUpdateRequestDto.getRegion();
    }

    public void cntAuction(){
        this.viewerCnt +=1L;
    }

    public void updateParticipantStatusOn() {
        this.participantStatus = true;
    }

    public void updateParticipantStatusOff() {
        this.participantStatus = false;
    }

    public void updateParticipantCnt(Long participantCnt) {
        this.participantCnt = participantCnt;
    }

    public void addAuctionTags(Tags tags) {
        this.tags =tags;
    }

    public void updateJoinPrice(Long userPrice){
        this.nowPrice = userPrice;
    }

    public void addAuctionChatRoomId(String roomId){
        this.chatRoomId = roomId;
    }


    public void addAuctionBidRoomId(String bidRoomId) {
        this.bidRoomId = bidRoomId;
    }

    public void addAuctionOnoRoomId(String onoRoomId) {
        this.onoRoomId = onoRoomId;
    }

    public LocalDateTime calcDeadLine(LocalDateTime now, Long auctionPeriod) {
        LocalDateTime deadline = now.plusMinutes(auctionPeriod);
        return deadline;
    }

    public void changeAuctionStatus(boolean auctionStatus){
        this.auctionStatus = auctionStatus;
    }

    public void changeSellerDone() {
        this.sellerDone = true;
    }

    public void changeBidderDone() {
        this.bidderDone = true;
    }

    public void changeReviewDone() {
        this.reviewDone = true;
    }
}
