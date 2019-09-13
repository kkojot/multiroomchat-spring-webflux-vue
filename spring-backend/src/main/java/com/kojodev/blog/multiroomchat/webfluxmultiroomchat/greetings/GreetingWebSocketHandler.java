package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.greetings;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GreetingWebSocketHandler implements WebSocketHandler {

    private final GreetingsService greetingsService = new GreetingsService();
    private final GreetingsPublisher greetingsPublisher;
    private final Flux<String> publisher;

    public GreetingWebSocketHandler(GreetingsPublisher greetingsPublisher) {
        this.greetingsPublisher = greetingsPublisher;
        this.publisher = Flux.create(greetingsPublisher).share();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        /* share greetings across websocket sessions */
        webSocketSession
                .receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .map(helloMessage -> greetingsService.greeting(helloMessage))
                .map(greetings -> greetingsPublisher.push(greetings))
                .subscribe();

        return webSocketSession.send(publisher
                .map(greetings -> webSocketSession.textMessage(greetings)));


        /* not sharing greetings across websocket sessions */
//        return webSocketSession.send(
//                webSocketSession.receive()
//                        .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
//                        .map(helloMessage -> greetingsService.greeting(helloMessage))
////                        .map(greetings -> greetingsPublisher.push(greetings))
//                        .doOnNext(greetings -> greetingsPublisher.push(greetings))
//                        .flatMap(greetings -> publisher
//                                .map(publisherGreetings -> webSocketSession.textMessage(publisherGreetings)))
//        );

        /*  not sharing flux across sessions */
//        return webSocketSession.send(
//                webSocketSession.receive()
//                        .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
//                        .map(helloMessage -> greetingsService.greeting(helloMessage))
//                        .flatMap(greetings -> Flux.just(greetings).share())
//                        .map(greetings -> webSocketSession.textMessage(greetings))
//        );

        /* not sharing greetings across websocket sessions */
//        return webSocketSession.send(
//                webSocketSession
//                        .receive()
//                        .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
//                        .map(helloMessage -> greetingsService.greeting(helloMessage))
//                        .map(greetings -> webSocketSession.textMessage(greetings))
//        );

    }
}
