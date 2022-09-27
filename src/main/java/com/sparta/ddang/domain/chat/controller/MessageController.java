package com.sparta.ddang.domain.chat.controller;


import com.sparta.ddang.domain.chat.dto.BidMessageDto;
import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

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
    
    
    // 경매 채팅 주소
    @MessageMapping("/chat/message")
    public void enter(ChatMessageDto message) {
        chatService.save(message);
    }

    // 호가 주소
    @MessageMapping("/chat/bid")
    public void enterBid(BidMessageDto message) {
        chatService.saveBid(message);
    }

    
    // 채팅 기록 복원
//    @GetMapping("/chat/message/{roomId}")
//    public ResponseDto<?> getMessage(@PathVariable String roomId) {
//        return chatService.getMessages(roomId);
//    }

    //이전 채팅 기록 조회
    @RequestMapping(value = "/chat/message/{roomId}", method = RequestMethod.GET)
    public ResponseDto<?> getMessage(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }


}
