package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.team;

import io.vavr.control.Option;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

public class SimpleTeamMessageResolver {

    private final WebSocketSession webSocketSession;
    private final String message;

    public SimpleTeamMessageResolver(WebSocketSession webSocketSession, String message) {
        this.webSocketSession = webSocketSession;
        this.message = message;
    }

    public static Flux<String> filterTeamMessage(WebSocketSession session, Flux<String> messages) {
        final String teamColor = teamColorFromSession(session);
        if (teamColor.equals("UNKNOWN")) return messages;
        final Flux<String> filtered = messages
                .filter(message -> message.startsWith(teamColor));
        return filtered;
    }

    private static String teamColorFromSession(WebSocketSession session) {
        return Option.of(session
                .getAttributes()
                .get("team"))
                .map(color -> color.toString())
                .getOrElse("UNKNOWN");
    }

    public String process() {
        if (message.startsWith("join:")) {
            return handleJoin();
        } else {
            return handleMessage();
        }
    }

    private String handleJoin() {
        final String teamColor = teamColorFromMessage();
        webSocketSession
                .getAttributes()
                .put("team", teamColor);
        return "New guy joined the " + teamColor + " team.";
    }

    private String handleMessage() {
        final String teamColor = teamColorFromSession(webSocketSession);
        return teamColor + ": " + message;
    }

    public String teamColorFromMessage() {
        return Option.of(message.split(":"))
                .map(strings -> strings.length > 1 ? strings[1] : "BLUE")
                .map(color -> color.toUpperCase().trim().equals("RED") ? "RED" : "BLUE")
                .getOrElse("BLUE");
    }


}
