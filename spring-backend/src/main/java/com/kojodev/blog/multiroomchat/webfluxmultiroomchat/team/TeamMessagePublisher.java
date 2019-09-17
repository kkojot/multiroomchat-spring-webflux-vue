package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.team;

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
public class TeamMessagePublisher implements Consumer<FluxSink<String>> {
    private static final Logger log = LoggerFactory.getLogger(TeamMessagePublisher.class);

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public boolean push(String message) {
        return queue.offer(message);
    }

    @Override
    public void accept(FluxSink<String> sink) {
        this.executor.execute(() -> {
            while (true) {
                Try.of(() -> {
                    final String message = queue.take();
                    return sink.next(message);
                })
                        .onFailure(ex -> log.error("Could not take team message from queue", ex));
            }
        });
    }
}
