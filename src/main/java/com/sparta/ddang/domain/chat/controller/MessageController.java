package com.sparta.ddang.domain.chat.controller;


import com.sparta.ddang.domain.chat.dto.BidMessageDto;
import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations sendingOperations;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void enter(ChatMessageDto message) {
        chatService.save(message);
    }

    @MessageMapping("/chat/bid")
    public void enterBid(BidMessageDto message) {
        chatService.saveBid(message);
    }

    @RequestMapping(value = "/chat/message/{roomId}", method = RequestMethod.GET)
    public ResponseDto<?> getMessage(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }

}
