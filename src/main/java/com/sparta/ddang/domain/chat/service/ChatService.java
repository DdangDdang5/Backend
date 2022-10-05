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
import com.sparta.ddang.domain.participant.entity.Participant;
import com.sparta.ddang.domain.participant.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

    @Transactional
    public void save(ChatMessageDto message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomService.enterChatRoom(message.getRoomId());
        }

        Optional<Member> member = memberRepository.findByNickName(message.getSender());
        String nickName = member.get().getNickName();
        String profileImg = member.get().getProfileImgUrl();

        LocalDateTime now = LocalDateTime.now();
        String createdAtString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));

        ChatMessage chatMessage = new ChatMessage(message, nickName, profileImg, createdAtString);
        ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(chatMessage.getRoomId());
        chatMessage.addChatRoomName(chatRoom.getRoomName());

        chatMessageJpaRepository.save(chatMessage);
        ChatMessage chatMessageRedis = chatMessageService.save(chatMessage);

        redisPublisher.publish(ChatRoomService.getTopic(chatMessage.getRoomId()), chatMessage);
    }

    @Transactional
    public void saveBid(BidMessageDto message) {
        if (BidMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomService.enterChatRoom(message.getRoomId());
        } else {
            Member member = memberRepository.findByNickName(message.getSender()).orElseThrow(
                    () -> new IllegalArgumentException("해당 닉네임 없음")
            );

            String nickName = member.getNickName();
            LocalDateTime now = LocalDateTime.now();
            String createdAtString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));

            BidMessage bidMessage = new BidMessage(message, nickName, createdAtString);

            bidMessageRepository.save(bidMessage);
            BidMessage bidMessageRedis = chatMessageService.saveBid(bidMessage);

            Long nowPrice = Long.parseLong(bidMessage.getMessage());
            Auction auction = auctionRepository.findByBidRoomId(bidMessage.getRoomId());
            auction.updateJoinPrice(nowPrice);

            if (participantRepository.existsByMemberIdAndAuctionId(member.getId(), auction.getId())) {
                auctionRepository.save(auction);
                JoinPrice joinPrice = new JoinPrice(member.getId(), auction.getId(), nowPrice);
                joinPriceRepository.save(joinPrice);
                redisPublisher.publishBid(ChatRoomService.getTopic(bidMessage.getRoomId()), bidMessage);

            } else {
                Participant participant = new Participant(member, auction);
                participantRepository.save(participant);
                Long participantCnt = participantRepository.countAllByAuctionId(auction.getId());
                auction.updateParticipantCnt(participantCnt);
                auctionRepository.save(auction);

                JoinPrice joinPrice = new JoinPrice(member.getId(), auction.getId(), nowPrice);
                joinPriceRepository.save(joinPrice);

                redisPublisher.publishBid(ChatRoomService.getTopic(bidMessage.getRoomId()), bidMessage);
            }
        }
    }

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    public List<ChatRoomDto> findAllRoom() {
        List<ChatRoomDto> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);
        return result;
    }

    public ChatRoomDto findById(String roomId) {
        return chatRooms.get(roomId);
    }

    public ResponseDto<?> getMessages(String roomId) {
        return chatMessageService.findAllMessage(roomId);
    }

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

    @Transactional
    public ResponseDto<?> getOnoMessages(String nickname) {
        String ono = "1:1";
        List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByNickNameAndRoomNameContainingOrderByCreatedAtDesc(nickname, ono);

        for (ChatMessage chatMessage : chatMessages) {
            List<ChatMessage> messageList = chatMessageJpaRepository.findAllByRoomId(chatMessage.getRoomId());
            ChatMessage lastChat = messageList.get(messageList.size() - 1);
            ArrayList<ChatMessage> lastchat1 = new ArrayList<>();

            if (lastChat.getMessage().equals("")) {
                for (int i = 0; i < messageList.size(); i++) {
                    if (!messageList.get(messageList.size() - 1 - i).getMessage().equals("")) {
                        lastchat1.add(messageList.get(messageList.size() - 1 - i));
                        break;
                    }
                }

                lastChat = lastchat1.get(0);
                if (!onoChatMessageRepository.existsByRoomId(lastChat.getRoomId())) {
                    Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());
                    OnoChatMessage onoChatMessage =
                            new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                                    lastChat.getNickName(), lastChat.getMessage(),
                                    lastChat.getProfileImgUrl(), auction.getId(),
                                    auction.getMember().getNickName(), nickname, lastChat.getCreatedAt());
                    onoChatMessageRepository.save(onoChatMessage);

                } else {
                    onoChatMessageRepository.deleteAllByRoomId(lastChat.getRoomId());
                    Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());
                    OnoChatMessage onoChatMessage =
                            new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                                    lastChat.getNickName(), lastChat.getMessage(),
                                    lastChat.getProfileImgUrl(), auction.getId(),
                                    auction.getMember().getNickName(), nickname, lastChat.getCreatedAt());
                    onoChatMessageRepository.save(onoChatMessage);
                }

            } else {
                if (!onoChatMessageRepository.existsByRoomId(lastChat.getRoomId())) {
                    Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());
                    OnoChatMessage onoChatMessage =
                            new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                                    lastChat.getNickName(), lastChat.getMessage(),
                                    lastChat.getProfileImgUrl(), auction.getId(),
                                    auction.getMember().getNickName(), nickname, lastChat.getCreatedAt());
                    onoChatMessageRepository.save(onoChatMessage);

                } else {
                    onoChatMessageRepository.deleteAllByRoomId(lastChat.getRoomId());
                    Auction auction = auctionRepository.findByOnoRoomId(lastChat.getRoomId());
                    OnoChatMessage onoChatMessage =
                            new OnoChatMessage(lastChat.getRoomId(), lastChat.getRoomName(),
                                    lastChat.getNickName(), lastChat.getMessage(),
                                    lastChat.getProfileImgUrl(), auction.getId(),
                                    auction.getMember().getNickName(), nickname, lastChat.getCreatedAt());
                    onoChatMessageRepository.save(onoChatMessage);
                }
            }
        }

        List<OnoChatMessage> onoChatMessages = onoChatMessageRepository.findAllByOrderByLastMessageTimeDesc();
        List<OnoChatMessageDto> onoChatMessageDtos = new ArrayList<>();

        for (OnoChatMessage onoChatMessage : onoChatMessages) {
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
                                .lastMessageTime(onoChatMessage.getLastMessageTime())
                                .auctionId(auction.getId())
                                .auctionTitle(auction.getTitle())
                                .multiImages(auction.getMultiImages())
                                .build()
                );
            }
        }

        return ResponseDto.success(onoChatMessageDtos);
    }

}
