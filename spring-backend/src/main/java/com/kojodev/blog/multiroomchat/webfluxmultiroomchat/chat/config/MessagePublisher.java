package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.config;

import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket.WebSocketChatMessage;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class MessagePublisher implements Consumer<FluxSink<WebSocketChatMessage>> {
    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private final BlockingQueue<WebSocketChatMessage> queue = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public boolean push(WebSocketChatMessage message) {
        return queue.offer(message);
    }

    @Override
    public void accept(FluxSink<WebSocketChatMessage> sink) {
        this.executor.execute(() -> {
            while (true) {
                Try.of(() -> {
                    final WebSocketChatMessage message = queue.take();
                    return sink.next(message);
                })
                        .onFailure(ex -> log.error("Could not take message from queue", ex));
            }
        });
    }
}
