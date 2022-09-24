package com.sparta.ddang.domain.chat.pubsub;

import com.sparta.ddang.domain.chat.entity.BidMessage;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    public void publishBid(ChannelTopic topic, BidMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        //redisTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}
