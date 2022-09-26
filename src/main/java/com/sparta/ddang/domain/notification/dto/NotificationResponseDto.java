package com.sparta.ddang.domain.notification.dto;

import com.sparta.ddang.domain.notification.entity.Notification;
import com.sparta.ddang.domain.notification.entity.NotificationType;
import lombok.Getter;

@Getter
public class NotificationResponseDto {

    private Long notificationId;
    private String content;
    private NotificationType notificationType;
    private Boolean isRead;

    public NotificationResponseDto() {}

    public NotificationResponseDto(Long notificationId, String content,
                                   NotificationType notificationType, Boolean isRead) {
        this.notificationId = notificationId;
        this.content = content;
        this.notificationType = notificationType;
        this.isRead = isRead;
    }

    public static NotificationResponseDto create(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getContent(),
                notification.getNotificationType(),
                notification.getIsRead()
        );
    }
}
