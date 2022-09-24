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
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashChatRoomDto;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    //private Map<String, ChannelTopic> topics;

    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private static ValueOperations<String, String> topics;
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        //topics = new HashMap<>();
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

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
//    public ChatRoom createChatRoom(String name) {
//        ChatRoom chatRoom = ChatRoom.create(name);
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }


    public ChatRoom checkRoom(String roomName) {
        Optional<ChatRoom> optionalChatRoom = chatRoomJpaRepository.findByRoomName(roomName);
        return optionalChatRoom.orElse(null);
    }

    // 낙찰자 조회 및 낙찰자와 판매자 채팅방 개설
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

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */

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
        System.out.println("=================================getTopic : " +roomId);

        //String topicToString = topics.get(roomId);

        //System.out.println("=================================topicToString : " +topicToString);

        //return new ChannelTopic(topicToString);
        return new ChannelTopic(roomId);
    }

//    public void enterChatRoom(String roomId) {
//        ChannelTopic topic = topics.get(roomId);
//        if (topic == null)
//            topic = new ChannelTopic(roomId);
//        redisMessageListener.addMessageListener(redisSubscriber, topic);
//        topics.put(roomId, topic);
//    }

//    public ChannelTopic getTopic(String roomId) {
//        return topics.get(roomId);
//    }
}
