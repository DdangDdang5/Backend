package com.sparta.ddang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Config가 있어야 websocket이 연결이 된다.
    // 따라서 아래 addEndpoint가 url이 있고 origin *가 있는 것.
    // "sockJs" :  --> "WebSocket Emulation"을 지원하는 것
    // WebSocket Emulation : WebSocket 연결이 실패한 경우에는 HTTP-Streaming, HTTP Long Polling
    // 같은 HTTP 기반의 다른 기술로 전환해 다시 연결을 시도하는 것을 말한다.
    // 즉 WebSocket Emulation을 통해서, 위와 같이 WebSocket 연결을 할 수 없는 경우에는
    // 다른 HTTP 기반의 기술을 시도하는 방법이다.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Heartbeat Message : 프록시가 커넥션이 끊겼다고 판단하지 않도록 한다

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/queue", "/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }


}
