package com.sparta.ddang.domain.chat.controller;

import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.chat.service.ChatRoomService;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;

    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @RequestMapping(value = "/room", method = RequestMethod.GET)
    public String rooms(Model model) {

        return "chat/room";

    }

    // 모든 채팅방 목록 반환
    @RequestMapping(value = "/rooms", method = RequestMethod.GET)
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRoomService.findAllRoom();
    }

    @RequestMapping(value = "/rooms/all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto<?> roomAll() {
        return chatService.findAllRoomAll();
    }

    // 채팅방 생성
//    @PostMapping("/room")
//    @ResponseBody
//    public ChatRoomDto createRoom(@RequestParam String name) {
//        return chatService.createRoom(name);
//    }

    // 채팅방 입장 화면
    @RequestMapping(value = "/room/enter/{roomId}", method = RequestMethod.GET)
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }

    // 특정 채팅방 조회
    @RequestMapping(value = "/room/{roomId}", method = RequestMethod.GET)
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable String roomId) {
        return chatService.findById(roomId);
    }



}