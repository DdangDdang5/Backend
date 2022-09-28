package com.sparta.ddang.domain.notification.controller;

import com.sparta.ddang.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @RequestMapping(value = "/subscribe/{memberId}", produces = "text/event-stream", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@PathVariable Long memberId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(memberId, lastEventId);
    }
}
