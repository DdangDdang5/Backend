package com.sparta.ddang.domain.chat.controller;


import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatService chatService;

//    @MessageMapping("/chat/message")
//    public void enter(ChatMessage message) {
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//        }
//        sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomId(),message);
//    }

    @MessageMapping("/chat/message")
    public void enter(ChatMessageDto message) {
        chatService.save(message);
    }




}