package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.team;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TeamMessageWebSocketHandler implements WebSocketHandler {

    private final TeamMessagePublisher teamMessagePublisher;
    private final Flux<String> publisher;

    public TeamMessageWebSocketHandler(TeamMessagePublisher teamMessagePublisher) {
        this.teamMessagePublisher = teamMessagePublisher;
        this.publisher = Flux.create(teamMessagePublisher).share();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        /* share greetings across websocket sessions */
        webSocketSession
                .receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .map(message -> new SimpleTeamMessageResolver(webSocketSession, message))
                .map(resolver -> resolver.process())
                .map(message -> teamMessagePublisher.push(message))
                .subscribe();

        return webSocketSession.send(
                SimpleTeamMessageResolver
                        .filterTeamMessage(webSocketSession, publisher)
                        .map(message -> webSocketSession.textMessage(message)));
    }
}
