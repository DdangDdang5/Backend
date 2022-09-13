package com.sparta.ddang.domain.chat.controller;

<<<<<<< HEAD
import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
=======

import com.sparta.ddang.domain.chat.entity.ChatRoom;
>>>>>>> main
import com.sparta.ddang.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
<<<<<<< HEAD

        return "chat/room";

=======
        return "chat/room";
>>>>>>> main
    }

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
<<<<<<< HEAD
    public List<ChatRoomDto> room() {
=======
    public List<ChatRoom> room() {
>>>>>>> main
        return chatService.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
<<<<<<< HEAD
    public ChatRoomDto createRoom(@RequestParam String name) {
=======
    public ChatRoom createRoom(@RequestParam String name) {
>>>>>>> main
        return chatService.createRoom(name);
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
<<<<<<< HEAD
    public ChatRoomDto roomInfo(@PathVariable String roomId) {
=======
    public ChatRoom roomInfo(@PathVariable String roomId) {
>>>>>>> main
        return chatService.findById(roomId);
    }

}