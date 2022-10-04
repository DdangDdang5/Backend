package com.sparta.ddang.domain.chat.service;


import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.chat.pubsub.RedisSubscriber;
import com.sparta.ddang.domain.chat.repository.ChatRoomJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashChatRoomDto;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private static ValueOperations<String, String> topics;
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = stringRedisTemplate.opsForValue();
    }

    public List<ChatRoom> findAllRoom() {
        List<ChatRoom> result = new ArrayList<>(opsHashChatRoom.values(CHAT_ROOMS));
        Collections.reverse(result);
        return result;
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public ChatRoom checkRoom(String roomName) {
        Optional<ChatRoom> optionalChatRoom = chatRoomJpaRepository.findByRoomName(roomName);
        return optionalChatRoom.orElse(null);
    }

    @Transactional
    public ChatRoomDto createRoom(String roomName) {
        ChatRoom existChatRoom = checkRoom(roomName);

        if (existChatRoom == null) {
            ChatRoomDto chatRoomDto = ChatRoomDto.create(roomName);
            ChatRoom chatRoom = new ChatRoom(chatRoomDto);

            // redis 저장
            opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
            redisTemplate.expire(CHAT_ROOMS, 48, TimeUnit.HOURS);

            // db 저장
            chatRoomJpaRepository.save(chatRoom);

            return chatRoomDto;
        }

        String existChatRoomId = existChatRoom.getRoomId();
        String existChatRoomName = existChatRoom.getRoomName();

        return ChatRoomDto.builder()
                .roomId(existChatRoomId)
                .roomName(existChatRoomName)
                .build();
    }

    public void enterChatRoom(String roomId) {
        if (topics.get(roomId) == null) {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.set(roomId, topic.toString());
            redisTemplate.expire(roomId, 48, TimeUnit.HOURS);
        } else {
            String topicToString = topics.get(roomId);
            ChannelTopic topic = new ChannelTopic(topicToString);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
        }
    }

    public static ChannelTopic getTopic(String roomId) {
        return new ChannelTopic(roomId);
    }

}
