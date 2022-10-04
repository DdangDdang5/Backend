package com.sparta.ddang.domain.chat.service;

import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.entity.BidMessage;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.repository.ChatMessageJpaRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    public static final String ENTER_INFO = "ENTER_INFO";
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessageDto;
    private HashOperations<String, String, List<ChatMessage>> opsHashChatMessage;
    private HashOperations<String, String, List<BidMessage>> opsHashChatBidMessage;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        opsHashChatBidMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessage.getRoomId();
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessage);

        //redis 의 hashes 자료구조
        //key : CHAT_MESSAGE , filed : roomId, value : chatMessageList
        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        redisTemplate.expire(CHAT_MESSAGE,24, TimeUnit.HOURS);

        return chatMessage;
    }

    @Transactional
    public BidMessage saveBid(BidMessage bidMessage) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(BidMessage.class));
        String roomId = bidMessage.getRoomId();
        List<BidMessage> bidMessageList = opsHashChatBidMessage.get(CHAT_MESSAGE, roomId);

        if (bidMessageList == null) {
            bidMessageList = new ArrayList<>();
        }
        bidMessageList.add(bidMessage);

        //redis 의 hashes 자료구조
        //key : CHAT_MESSAGE , filed : roomId, value : chatMessageList
        opsHashChatBidMessage.put(CHAT_MESSAGE, roomId, bidMessageList);
        redisTemplate.expire(CHAT_MESSAGE,24, TimeUnit.HOURS);

        return bidMessage;
    }

    @Transactional
    public ResponseDto<?> findAllMessage(String roomId) {
        List<ChatMessage> chatMessageList = new ArrayList<>();

        if (opsHashChatBidMessage.size(CHAT_MESSAGE) > 0) {
            return ResponseDto.success (opsHashChatBidMessage.get(CHAT_MESSAGE, roomId));
        }

        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
            return ResponseDto.success (opsHashChatMessage.get(CHAT_MESSAGE, roomId));
        } else {
            List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);

            for (ChatMessage chatMessage : chatMessages) {
                LocalDateTime createdAt = chatMessage.getCreatedAt();
                String createdAtString = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
                ChatMessage chatMessage1 = new ChatMessage(chatMessage);
                chatMessageList.add(chatMessage1);
            }

            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);

            return ResponseDto.success(chatMessageList);
        }
    }

}
