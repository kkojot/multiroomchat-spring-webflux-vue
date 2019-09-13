package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.app.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class WebSocketMessage {

    public final WebSocketMessageType type;
    public final JsonNode payload;

    @JsonCreator
    public WebSocketMessage(@JsonProperty("type") WebSocketMessageType type,
                            @JsonProperty("payload") JsonNode payload) {
        this.type = type;
        this.payload = payload;
    }
}
