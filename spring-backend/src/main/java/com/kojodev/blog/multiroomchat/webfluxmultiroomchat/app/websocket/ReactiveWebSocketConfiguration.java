package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket;

import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.config.MessageWebSocketHandler;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.greetings.GreetingWebSocketHandler;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.team.TeamMessageWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.server.WebHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveWebSocketConfiguration {

    private WebSocketHandler greetingWebSocketHandler;
    private WebSocketHandler messageWebSocketHandler;
    private WebSocketHandler teamMessageWebSocketHandler;

    public ReactiveWebSocketConfiguration(GreetingWebSocketHandler greetingWebSocketHandler,
                                          MessageWebSocketHandler messageWebSocketHandler,
                                          TeamMessageWebSocketHandler teamMessageWebSocketHandler) {
        this.greetingWebSocketHandler = greetingWebSocketHandler;
        this.messageWebSocketHandler = messageWebSocketHandler;
        this.teamMessageWebSocketHandler = teamMessageWebSocketHandler;
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/greetings", greetingWebSocketHandler);
        map.put("/chat", messageWebSocketHandler);
        map.put("/team", teamMessageWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
