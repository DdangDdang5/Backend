package com.sparta.ddang.domain.notification.entity;

import lombok.Getter;

@Getter
public class NotificationContent {
    private String content;

    public NotificationContent() {}

    public NotificationContent(String content) {
        this.content = content;
    }
}
