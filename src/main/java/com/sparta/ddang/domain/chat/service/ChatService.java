package com.sparta.ddang.domain.chat.service;


import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.chat.dto.BidMessageDto;
import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.domain.chat.dto.ChatRoomResponseDto;
import com.sparta.ddang.domain.chat.entity.BidMessage;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.chat.repository.BidMessageRepository;
import com.sparta.ddang.domain.chat.repository.ChatMessageRepository;
import com.sparta.ddang.domain.chat.repository.ChatRoomRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final BidMessageRepository bidMessageRepository;

    private final SimpMessageSendingOperations sendingOperations;
    private Map<String, ChatRoomDto> chatRooms;

    private final MemberRepository memberRepository;

    private final AuctionRepository auctionRepository;




    //채팅방 생성 원본
//    @Transactional
//    public ChatRoomDto createRoom(String name) {
//        ChatRoomDto chatRoomDto = ChatRoomDto.create(name);
//
//        System.out.println("===========================");
//        System.out.println(chatRoomDto.getRoomId());
//        System.out.println(chatRoomDto.getRoomName());
//        System.out.println("===========================");
//
//        chatRooms.put(chatRoomDto.getRoomId(), chatRoomDto);
//
//        ChatRoom chatRoom = new ChatRoom(chatRoomDto);
//
//        chatRoomRepository.save(chatRoom);
//
//        return chatRoomDto;
//    }


    @Transactional
    public ChatRoomDto createRoom(String name) {
        ChatRoomDto chatRoomDto = ChatRoomDto.create(name);

        System.out.println("===========================");
        System.out.println(chatRoomDto.getRoomId());
        System.out.println(chatRoomDto.getRoomName());
        System.out.println("===========================");

        chatRooms.put(chatRoomDto.getRoomId(), chatRoomDto);

        ChatRoom chatRoom = new ChatRoom(chatRoomDto);

        chatRoomRepository.save(chatRoom);

        return chatRoomDto;
    }

    @Transactional
    public void save(ChatMessageDto message) {

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하였습니다.");
        }

        System.out.println("===============================================");
        System.out.println(message.getType());
        System.out.println(message.getRoomId());
        System.out.println(message.getSender());
        System.out.println(message.getMessage());
        System.out.println("===============================================");

        Optional<Member> member = memberRepository.findByNickName(message.getSender());

        String nickName = member.get().getNickName();

        System.out.println("닉네임" + nickName);

        String profileImg = member.get().getProfileImgUrl();

        System.out.println("프로필 이미지" + profileImg);

        ChatMessage chatMessage = new ChatMessage(message, nickName, profileImg);

        chatMessageRepository.save(chatMessage);

        System.out.println("===============================================");
        System.out.println(chatMessage.getType());
        System.out.println(chatMessage.getRoomId());
        System.out.println(chatMessage.getSender());
        System.out.println(chatMessage.getMessage());
        System.out.println(chatMessage.getCreatedAt());
        System.out.println(chatMessage.getNickName());
        System.out.println(chatMessage.getProfileImgUrl());
        System.out.println("===============================================");

        sendingOperations.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);


    }

    @Transactional
    public void saveBid(BidMessageDto message) {

        if (BidMessage.MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender() + "님이 입장하였습니다.");
            message.setMessage("0");
        }

        System.out.println("===============================================");
        System.out.println(message.getType());
        System.out.println(message.getRoomId());
        System.out.println(message.getSender());
        System.out.println(message.getMessage());
        System.out.println("===============================================");

        Optional<Member> member = memberRepository.findByNickName(message.getSender());

        String nickName = member.get().getNickName();

        System.out.println("닉네임" + nickName);

        BidMessage bidMessage = new BidMessage(message, nickName);

        bidMessageRepository.save(bidMessage);

        Long nowPrice = Long.parseLong(bidMessage.getMessage());

        Auction auction = auctionRepository.findByBidRoomId(bidMessage.getRoomId());

        //Auction auction = auctionRepository.findByMember(member);

        auction.updateJoinPrice(nowPrice);

        auctionRepository.save(auction);

        System.out.println("===============================================");
        System.out.println(bidMessage.getType());
        System.out.println(bidMessage.getRoomId());
        System.out.println(bidMessage.getSender());
        System.out.println(bidMessage.getMessage());
        System.out.println(bidMessage.getCreatedAt());
        System.out.println(bidMessage.getNickName());
        System.out.println("===============================================");

        sendingOperations.convertAndSend("/topic/chat/room/" + bidMessage.getRoomId(), bidMessage);

    }


    @PostConstruct // 최초 한번만 Bean 주입하는 어노테이션 반복 주입할 필요없다
    // 왜? 소켓으로 연결되어 있는데 계속 빈을 주입할 필요는 없다.
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 불러오기
    public List<ChatRoomDto> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoomDto> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);

        return result;
    }

    //채팅방 하나 불러오기
    public ChatRoomDto findById(String roomId) {
        return chatRooms.get(roomId);
    }

    @Transactional
    public ResponseDto<?> getMessages(String roomId) {

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomId(roomId);

        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {

            chatMessageDtos.add(
                    ChatMessageDto.builder()
                            .type(chatMessage.getType())
                            .roomId(chatMessage.getRoomId())
                            .sender(chatMessage.getSender())
                            .message(chatMessage.getMessage())
                            .createdAt(chatMessage.getCreatedAt())
                            .build()
            );
        }

        return ResponseDto.success(chatMessageDtos);


    }


    @Transactional
    public ResponseDto<?> findAllRoomAll() {

        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByOrderByCreatedAtDesc();

        List<ChatRoomResponseDto> chatRoomDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {

            chatRoomDtos.add(

                    ChatRoomResponseDto.builder()
                            .roomId(chatRoom.getRoomId())
                            .roomName(chatRoom.getRoomName())
                            .createdAt(chatRoom.getCreatedAt())
                            .build()

            );


        }

        return ResponseDto.success(chatRoomDtos);

    }



}
