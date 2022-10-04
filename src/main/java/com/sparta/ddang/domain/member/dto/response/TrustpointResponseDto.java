package com.sparta.ddang.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrustpointResponseDto {
    private Long memberId;
    private int trustPoint;
    private String trustGrade;

    @Builder
    public TrustpointResponseDto(Long memberId, int trustPoint, String trustGrade) {
        this.memberId = memberId;
        this.trustPoint = trustPoint;
        this.trustGrade = trustGrade;
    }

    public TrustpointResponseDto() {}
}
