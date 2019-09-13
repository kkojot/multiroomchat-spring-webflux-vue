package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.config;

import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.greetings.GreetingsPublisher;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.greetings.GreetingsService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private final GreetingsService greetingsService = new GreetingsService();
    private final GreetingsPublisher greetingsPublisher;
    private final Flux<String> publisher;

    public MessageWebSocketHandler(GreetingsPublisher greetingsPublisher) {
        this.greetingsPublisher = greetingsPublisher;
        this.publisher = Flux.create(greetingsPublisher).share();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(
                webSocketSession
                        .receive()
                        .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                        .map(message -> "hello, " + message)
                        .map(message -> webSocketSession.textMessage(message))
        );
    }
}
