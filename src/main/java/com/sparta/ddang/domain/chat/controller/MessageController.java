package com.sparta.ddang.domain.chat.controller;


<<<<<<< HEAD
import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.service.ChatService;
=======
import com.sparta.ddang.domain.chat.entity.ChatMessage;
>>>>>>> main
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

<<<<<<< HEAD
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




=======
    @MessageMapping("/chat/message")
    public void enter(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
        }
        sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomId(),message);
    }


>>>>>>> main
}
