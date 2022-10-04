package com.sparta.ddang.domain.notification.dto;

import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.notification.entity.NotificationType;
import lombok.Getter;

@Getter
public class NotificationRequestDto {
    private Member receiver;
    private NotificationType notificationType;
    private String content;
}
