package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.config;

import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket.WebSocketChatMessage;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.service.WebSocketMessageResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private final MessagePublisher messagePublisher;
    private final Flux<WebSocketChatMessage> publisher;

    public MessageWebSocketHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
        this.publisher = Flux.create(messagePublisher).share();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        webSocketSession
                .receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .map(payload -> WebSocketMessageResolver.message(payload))
                .map(webSocketMessage -> webSocketMessage
                        .map(message -> new WebSocketMessageResolver(message, webSocketSession))
                        .flatMap(webSocketMessageResolver -> webSocketMessageResolver.process()))
                .map(response -> response
                        .map(message -> messagePublisher.push(message)))
                .subscribe();

        return webSocketSession.send(publisher
                .map(message -> WebSocketMessageResolver.message(message))
                .map(messageJson -> webSocketSession.textMessage(messageJson)));


//        return webSocketSession.send(
//                webSocketSession
//                        .receive()
//                        .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
//                        .map(payload -> WebSocketMessageResolver.message(payload))
//                        .map(webSocketMessage -> webSocketMessage
//                                .map(message -> new WebSocketMessageResolver(message, webSocketSession))
//                                .flatMap(webSocketMessageResolver -> webSocketMessageResolver.process())
//                                .map(message -> webSocketSession.textMessage(message))
//                        );
    }
}
