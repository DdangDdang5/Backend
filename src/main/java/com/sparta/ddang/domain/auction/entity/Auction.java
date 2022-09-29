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
@DynamicInsert // 디폴트가 null일때 나머지만 insert
public class Auction extends Timestamped { // 19개

    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // 멈버 테이블을 매핑해서 경매 게시글과 게시글 작성자를 연동시키자.
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

//    @Column
//    @ColumnDefault("0")
//    private String profileImgUrl;

    @Column(nullable = false)
    private String title;

    @Column
    private String productName;

    @Column(nullable = false)
    private String content;

    //auctionImgUrl
    // orphanRemoval = true 공부하기 아니 고아 객체를 왜 추적을 하는거야? 고아가 있으면 제거하는거지
    // 무조건적으로 고아를 검색하는 건가?
    //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    @Column(nullable = false) //10개
    private String category;

    @Column(nullable = false)
    private String region;

    @Column
    private boolean direct; // true

    @Column
    private boolean delivery; // true

    //조회수 테이블 만들기
    @Column
    @ColumnDefault("0") //default 0
    private Long viewerCnt = 0L;

    // 참여자 수 만들기 - 채팅 기본원리 공부후
    @Column
    @ColumnDefault("0") //default 0
    private Long participantCnt = 0L;

    @Column
    private boolean auctionStatus; // true

    @Column
    private boolean participantStatus;

    @Column
    private boolean auctionDone;

    @Column
    private boolean reviewDone;

    @Column
    private String chatRoomId;

    @Column
    private String bidRoomId;

    @Column
    private String onoRoomId;


//    @Column
//    private boolean favoriteStatus;

    public Auction(){

    }


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
        this.auctionDone = false;
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

    public void changeAuctionDone() {
        this.auctionDone = true;
    }

    public void changeReviewDone() {
        this.reviewDone = true;
    }
}
