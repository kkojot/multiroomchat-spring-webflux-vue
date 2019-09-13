package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomMessageInDto {

    public final String roomKey;
    public final String message;

    @JsonCreator
    public RoomMessageInDto(@JsonProperty("roomKey") String roomKey,
                            @JsonProperty("message") String message) {
        this.roomKey = roomKey;
        this.message = message;
    }
}
