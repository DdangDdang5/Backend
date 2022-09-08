package com.sparta.ddang.domain.chat.controller;

import com.sparta.ddang.domain.chat.dto.ChatRoomListDto;
import com.sparta.ddang.domain.chat.dto.ChatRoomRequestDto;
import com.sparta.ddang.domain.chat.dto.ChatRoomResponseDto;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.service.ChatMessageService;
import com.sparta.ddang.domain.chat.service.ChatRoomService;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.service.MemberService;
import com.sparta.ddang.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;


    // 채팅방 생성
    @PostMapping("/channel")
    @ResponseBody
    public ChatRoomResponseDto createChatRoom(@RequestBody ChatRoomRequestDto requestDto) {
        log.info("채팅방 생성 requestDto = {}", requestDto);
//        requestDto.setMemberId(SecurityUtil.getCurrentMemberId());
        String email = SecurityUtil.getCurrentMemberEmail();
        log.info("현재 유저의 이메일 = {}", email);

        return chatRoomService.createChatRoom(requestDto);
    }


    // 전체 채팅방 목록 조회
    @GetMapping("/channels")
    @ResponseBody
    public List<ChatRoomListDto> getAllChatRooms() {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.checkEmail(email);

        return chatRoomService.getAllChatRooms(member);
    }


    // 특정 채팅방 조회
    @GetMapping("/channel/{roomId}")
    @ResponseBody
    public ChatRoomResponseDto showChatRoom(@PathVariable Long roomId) {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.checkEmail(email);
        return chatRoomService.showChatRoom(roomId, member);
    }

    //특정 채팅방 삭제
    @DeleteMapping("/channel/{roomId}")
    @ResponseBody
    public boolean deleteChatRoom(@PathVariable Long roomId){
        return chatRoomService.deleteChatRoom(roomId);
    }


    //채팅방 내 메시지 전체 조회
    @GetMapping("/channel/{roomId}/messages")
    @ResponseBody
    public List<ChatMessage> getRoomMessages(@PathVariable Long roomId) {
        return chatMessageService.getMessages(roomId);
    }
}
