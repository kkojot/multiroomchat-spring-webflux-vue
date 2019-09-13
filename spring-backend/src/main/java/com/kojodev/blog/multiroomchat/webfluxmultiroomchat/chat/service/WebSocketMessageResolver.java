package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.AppError;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket.WebSocketChatMessage;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket.WebSocketChatMessageType;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.domain.User;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.dto.UserRoomKeyDto;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.jackson.datatype.VavrModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.function.Function;

public class WebSocketMessageResolver {

    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageResolver.class);
    private static final RoomService roomService = new RoomService();
    private static final ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new VavrModule());
    }

    private final WebSocketChatMessage message;
    private final WebSocketSession webSocketSession;

    public WebSocketMessageResolver(WebSocketChatMessage message, WebSocketSession webSocketSession) {
        this.message = message;
        this.webSocketSession = webSocketSession;
    }

    public static Either<AppError, WebSocketChatMessage> message(String payload) {
        log.info("mamy message: {}", payload);
        return Try.of(() -> jsonMapper.readValue(payload, WebSocketChatMessage.class))
                .onFailure(exception -> log.error("Found invalid WebSocketMessage", exception))
                .toEither(AppError.INVALID_WEBSOCKET_MESSAGE);
    }

    public static String message(WebSocketChatMessage payload) {
        log.info("przerabiamy message na stringa");
        return Try.of(() -> jsonMapper.writeValueAsString(payload))
                .onFailure(exception -> log.error("Error parsing WebSocketChatMessage to JSON", exception))
                .getOrElse("");
    }

    public Either<AppError, WebSocketChatMessage> process() {
        log.info("probujemy process message");
        if (message.type == WebSocketChatMessageType.JOIN_CHAT) {
            log.info("join chat, user!");
            return Try.of(() -> {
//            Thread.sleep(1000); //simulate delay
                final User user = jsonMapper.treeToValue(message.payload, User.class);
                webSocketSession
                        .getAttributes()
                        .put("username", user.name);
                final JsonNode payloadNode = jsonMapper.valueToTree("user " + user.name + " joined the chat!");
                final WebSocketChatMessage webSocketMessage = new WebSocketChatMessage(WebSocketChatMessageType.JOIN_CHAT, payloadNode);
                return webSocketMessage;
            })
                    .onFailure(parserException -> log.error("Could not parse payload for message type {}", message.type, parserException))
                    .toEither(AppError.INVALID_PAYLOAD);
        } else if (message.type == WebSocketChatMessageType.JOIN_ROOM) {
            return Try.of(() -> {
                final UserRoomKeyDto payload = jsonMapper.treeToValue(message.payload, UserRoomKeyDto.class);
                return getUsername()
                        .map(username -> new UserRoomKeyDto(payload.roomKey, username))
                        .map(userRoomKey -> roomService.addUserToRoom(userRoomKey))
                        .map(chatRoomUserListEither -> chatRoomUserListEither
                                .map(chatRoomUserList -> (JsonNode) jsonMapper.valueToTree(chatRoomUserList))
                                .map(payloadNode -> new WebSocketChatMessage(WebSocketChatMessageType.JOIN_CHAT, payloadNode)))
                        .toEither(AppError.UNKNOWN_USER_SESSION)
                        .flatMap(Function.identity());

            })
                    .onFailure(parserException -> log.error("Could not parse payload for message type {}", message.type, parserException))
                    .toEither(AppError.INVALID_PAYLOAD)
                    .flatMap(Function.identity());
        } else {
            log.error("Could not resolve message type {}", message);
            return Either.left(AppError.INVALID_WEBSOCKET_MESSAGE);
        }
    }

    private Option<String> getUsername() {
        return Option.of(webSocketSession
                .getAttributes()
                .get("username"))
                .map(username -> username.toString());
    }
}

