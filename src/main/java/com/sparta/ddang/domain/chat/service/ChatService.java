package com.sparta.ddang.domain.chat.service;


import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.chat.dto.*;
import com.sparta.ddang.domain.chat.entity.BidMessage;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.chat.entity.OnoChatMessage;
import com.sparta.ddang.domain.chat.pubsub.RedisPublisher;
import com.sparta.ddang.domain.chat.repository.BidMessageRepository;
import com.sparta.ddang.domain.chat.repository.ChatMessageJpaRepository;
import com.sparta.ddang.domain.chat.repository.ChatRoomJpaRepository;
import com.sparta.ddang.domain.chat.repository.OnoChatMessageRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.joinprice.entity.JoinPrice;
import com.sparta.ddang.domain.joinprice.repository.JoinPriceRepository;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    private final ChatRoomService chatRoomService;

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    private final ChatMessageService chatMessageService;

    private final BidMessageRepository bidMessageRepository;

    private final SimpMessageSendingOperations sendingOperations;
    private Map<String, ChatRoomDto> chatRooms;

    private final MemberRepository memberRepository;

    private final AuctionRepository auctionRepository;

    private final JoinPriceRepository joinPriceRepository;

    private final OnoChatMessageRepository onoChatMessageRepository;


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

//    public ChatRoom checkRoom(String roomName) {
//        Optional<ChatRoom> optionalChatRoom = chatRoomJpaRepository.findByRoomName(roomName);
//        return optionalChatRoom.orElse(null);
//    }
//
//     //낙찰자 조회 및 낙찰자와 판매자 채팅방 개설
//    @Transactional
//    public ChatRoomDto createRoom(String roomName) {
//
//        ChatRoom existChatRoom = checkRoom(roomName);
//
//        if (existChatRoom == null) {
//            ChatRoomDto chatRoomDto = ChatRoomDto.create(roomName);
//
//            chatRooms.put(chatRoomDto.getRoomId(), chatRoomDto);
//
//            ChatRoom chatRoom = new ChatRoom(chatRoomDto);
//
//            chatRoomJpaRepository.save(chatRoom);
//
//            return chatRoomDto;
//        }
//
//        String existChatRoomId = existChatRoom.getRoomId();
//        String existChatRoomName = existChatRoom.getRoomName();
//
//        return ChatRoomDto.builder()
//                .roomId(existChatRoomId)
//                .roomName(existChatRoomName)
//                .build();
//    }

    @Transactional
    public void save(ChatMessageDto message) {

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomService.enterChatRoom(message.getRoomId());
            //message.setMessage(message.getSender() + "님이 입장하였습니다.");
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

        //redis저장시 db에 있는 createdAt, modifiedAt을 꺼내와서 넣지를 못함.
        // 아래 생성자로 생성한 데이터만 저장됨. 
        // timestamped에서 상속받는데도 불구하고 시간만 빼고 아래에 있는 데이터는 다 저장이됨.
        LocalDateTime now = LocalDateTime.now();
        String createdAtString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));

        ChatMessage chatMessage = new ChatMessage(message, nickName, profileImg,createdAtString);

        ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(chatMessage.getRoomId());

        System.out.println(chatRoom.getRoomName());

        chatMessage.addChatRoomName(chatRoom.getRoomName());

        //db에 저장
        chatMessageJpaRepository.save(chatMessage);

        //redis에 저장
        ChatMessage chatMessageRedis = chatMessageService.save(chatMessage);

        System.out.println("==================redis 시간 :============"+chatMessageRedis.getCreatedAt());


        System.out.println("===============================================");
        System.out.println(chatMessage.getType());
        System.out.println(chatMessage.getRoomId());
        System.out.println(chatMessage.getRoomName());
        System.out.println(chatMessage.getSender());
        System.out.println(chatMessage.getMessage());
        System.out.println(chatMessage.getCreatedAt());
        System.out.println(chatMessage.getNickName());
        System.out.println(chatMessage.getProfileImgUrl());
        System.out.println("===============================================");

        //sendingOperations.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);
        redisPublisher.publish(ChatRoomService.getTopic(chatMessage.getRoomId()), chatMessage);

        //redis에 저장
        //chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void saveBid(BidMessageDto message) {

        if (BidMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomService.enterChatRoom(message.getRoomId());
//            message.setMessage(message.getSender() + "님이 입장하였습니다.");
            //message.setMessage("0");
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

        LocalDateTime now = LocalDateTime.now();
        String createdAtString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));

        BidMessage bidMessage = new BidMessage(message, nickName,createdAtString);

        //db에 저장
        bidMessageRepository.save(bidMessage);

        //redis에 저장
        BidMessage bidMessageRedis = chatMessageService.saveBid(bidMessage);

        System.out.println("==================redis 시간 :============"+bidMessageRedis.getCreatedAt());

        Long nowPrice = Long.parseLong(bidMessage.getMessage());

        Auction auction = auctionRepository.findByBidRoomId(bidMessage.getRoomId());

        //Auction auction = auctionRepository.findByMember(member);

        auction.updateJoinPrice(nowPrice);

        auctionRepository.save(auction);

        JoinPrice joinPrice = new JoinPrice(member.get().getId(), auction.getId(), nowPrice);

        joinPriceRepository.save(joinPrice);


        System.out.println("===============================================");
        System.out.println(bidMessage.getType());
        System.out.println(bidMessage.getRoomId());
        System.out.println(bidMessage.getSender());
        System.out.println(bidMessage.getMessage());
        System.out.println(bidMessage.getCreatedAt());
        System.out.println(bidMessage.getNickName());
        System.out.println("===============================================");

        //sendingOperations.convertAndSend("/topic/chat/room/" + bidMessage.getRoomId(), bidMessage);

        System.out.println("================================bidMessage roomid :" + bidMessage.getRoomId());

        redisPublisher.publishBid(ChatRoomService.getTopic(bidMessage.getRoomId()), bidMessage);
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

    //redis에 저장되어있는 message 들 출력
    public ResponseDto<?> getMessages(String roomId) {
        return chatMessageService.findAllMessage(roomId);
    }


//    @Transactional
//    public ResponseDto<?> getMessages(String roomId) {
//
//        List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);
//
//        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
//
//        for (ChatMessage chatMessage : chatMessages) {
//
//            chatMessageDtos.add(
//                    ChatMessageDto.builder()
//                            .type(chatMessage.getType())
//                            .roomId(chatMessage.getRoomId())
//                            .sender(chatMessage.getSender())
//                            .message(chatMessage.getMessage())
//                            .profileImgUrl(chatMessage.getProfileImgUrl())
//                            .createdAt(chatMessage.getCreatedAt())
//                            .build()
//            );
//        }
//
//        return ResponseDto.success(chatMessageDtos);
//
//
//    }


    @Transactional
    public ResponseDto<?> findAllRoomAll() {

        List<ChatRoom> chatRoomList = chatRoomJpaRepository.findAllByOrderByCreatedAtDesc();

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

//    @Transactional
//    public ResponseDto<?> getOnoMessages(String nickname) {
//
//        String ono = "1:1";
//        List<ChatMessage> chatMessages = chatMessageRepository.findAllByNickNameAndRoomNameContainingOrderByCreatedAtDesc(nickname,ono);
//
//
//        List<OnoChatMessageDto> onoChatMessageDtos = new ArrayList<>();
//
//        for (ChatMessage chatMessage : chatMessages){
//
//            onoChatMessageDtos.add(
//
//                    OnoChatMessageDto.builder()
//                            .roomId(chatMessage.getRoomId())
//                            .roomName(chatMessage.getRoomName())
//                            .profileImg(chatMessage.getProfileImgUrl())
//                            .message(chatMessage.getMessage())
//                            .createdAt(chatMessage.getCreatedAt())
//                            .build()
//
//            );
//
//        }
//
//
//        return ResponseDto.success(onoChatMessageDtos);
//
//    }


    @Transactional
    public ResponseDto<?> getOnoMessages(String nickname) {

        String ono = "1:1";
        List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByNickNameAndRoomNameContainingOrderByCreatedAtDesc(nickname,ono);

        //List<OnoChatMessageDto> onoChatMessageDtos = new ArrayList<>();


        for (ChatMessage chatMessage : chatMessages){

            // 내가 참가한 그방의 마지막 메시지를 조회해옴
            List<ChatMessage> messageList = chatMessageJpaRepository.findAllByRoomId(chatMessage.getRoomId());
            ChatMessage lastChat = messageList.get(messageList.size()-1);

            if (!onoChatMessageRepository.existsByRoomId(lastChat.getRoomId())){
                //onoChatMessageRepository.deleteAllByRoomIdAndNickName(lastChat.getRoomId(), lastChat.getNickName());

                Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());
                // lastChat.getNickName() : 마지막 발언자
                //  auction.getMember().getNickName() : 경매 게시자
                //  nickname : chatmessage에서 nickname을 검색한 채팅방에서
                //  nickname이 있는 채팅방에서 닉네임을 포함안 onoChatMessage 새로 생성한후 저장
                // 그러면 만약 그채팅방에 메시지를 입력하면 ChatMessage에 nickname에 해당하는
                // 채팅방Id가 있을 것이고
                // nickname을 검색했을때 onoChatMessage에 닉네임이 포함되어 저장되면서 해당 닉네임이 있는
                // 채팅방을 검색할 수 있다.
                OnoChatMessage onoChatMessage
                        = new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                        lastChat.getNickName() ,lastChat.getMessage(),
                        lastChat.getProfileImgUrl(),auction.getId(),
                        auction.getMember().getNickName(),nickname);

                onoChatMessageRepository.save(onoChatMessage);

            } else {

                onoChatMessageRepository.deleteAllByRoomId(lastChat.getRoomId());

                Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());

                OnoChatMessage onoChatMessage
                        = new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                        lastChat.getNickName() ,lastChat.getMessage(),
                        lastChat.getProfileImgUrl(),auction.getId(),
                        auction.getMember().getNickName(),nickname);

                onoChatMessageRepository.save(onoChatMessage);

            }

        }

        // 방번호를가져와야 되는데 어떻게 가져오지?

        List<OnoChatMessage> onoChatMessages = onoChatMessageRepository.findAll();
        //OnoChatMessage lastChat = onoChatMessages.get(onoChatMessages.size()-1);

        List<OnoChatMessageDto> onoChatMessageDtos = new ArrayList<>();

        for (OnoChatMessage onoChatMessage : onoChatMessages){

            Auction auction = auctionRepository.findById(onoChatMessage.getAuctionId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물 없음.")
            );

            if (onoChatMessage.getSeller().equals(nickname) || onoChatMessage.getBidder().equals(nickname)) {

                onoChatMessageDtos.add(

                        OnoChatMessageDto.builder()
                                .roomId(onoChatMessage.getRoomId())
                                .roomName(onoChatMessage.getRoomName())
                                .message(onoChatMessage.getMessage())
                                .profileImg(onoChatMessage.getProfileImgUrl())
                                .createdAt(onoChatMessage.getCreatedAt())
                                .auctionId(auction.getId())
                                .auctionTitle(auction.getTitle())
                                .multiImages(auction.getMultiImages())
                                .build()

                );

            }

        }

        return ResponseDto.success(onoChatMessageDtos);

    }


//    @Transactional
//    public ResponseDto<?> getOnoMessages(String nickname) {
//
//        String ono = "1:1";
//        List<ChatMessage> chatMessages = chatMessageRepository.findAllByNickNameAndRoomNameContainingOrderByCreatedAtDesc(nickname,ono);
//
//
//        List<OnoChatMessageDto> onoChatMessageDtos = new ArrayList<>();
//
//        List<ChatMessage> lastMessages = getLastMessage(chatMessages);
//
//        for (ChatMessage lastMessage : lastMessages) {
//            onoChatMessageDtos.add(
//
//                    OnoChatMessageDto.builder()
//                            .roomId(lastMessage.getRoomId())
//                            .roomName(lastMessage.getRoomName())
//                            .message(lastMessage.getMessage())
//                            .profileImg(lastMessage .getProfileImgUrl())
//                            .createdAt(lastMessage .getCreatedAt())
//                            .build()
//
//            );
//
//        }
//
//        return ResponseDto.success(onoChatMessageDtos);
//
//    }
//
//
//    public List<ChatMessage> getLastMessage(List<ChatMessage> chatMessages) {
//
//        List<ChatMessage> lastMessages = new ArrayList<>();
//
//        for (ChatMessage chatMessage : chatMessages){
//
//            List<ChatMessage> messageList = chatMessageRepository.findAllByRoomId(chatMessage.getRoomId());
//            ChatMessage lastMessage = messageList.get(messageList.size()-1);
//            lastMessages.add(lastMessage);
//        }
//
//        return lastMessages;
//    }



}
