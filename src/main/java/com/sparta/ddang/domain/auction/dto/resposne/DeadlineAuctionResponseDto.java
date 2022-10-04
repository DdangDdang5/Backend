package com.sparta.ddang.domain.auction.dto.resposne;

import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DeadlineAuctionResponseDto {
    private String title;
    private String content;
    private Long nowPrice;
    private List<MultiImage> multiImages;
    private Long memberId;
    private Long auctionId;
    private boolean direct;
    private boolean delivery;
    private String region;
    private LocalDateTime deadline;

    public DeadlineAuctionResponseDto() {}

    @Builder
    public DeadlineAuctionResponseDto(String title, String content, Long nowPrice,
                                      List<MultiImage> multiImages, Long memberId, Long auctionId,
                                      boolean direct, boolean delivery, String region, LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        this.nowPrice = nowPrice;
        this.multiImages = multiImages;
        this.memberId = memberId;
        this.auctionId = auctionId;
        this.direct = direct;
        this.delivery = delivery;
        this.region = region;
        this.deadline = deadline;
    }
}
