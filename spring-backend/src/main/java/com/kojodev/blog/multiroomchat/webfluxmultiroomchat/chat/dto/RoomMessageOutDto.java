package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomMessageOutDto {

    public final String roomKey;
    public final String message;
    public final String userName;

    @JsonCreator
    public RoomMessageOutDto(@JsonProperty("roomKey") String roomKey,
                             @JsonProperty("message") String message,
                             @JsonProperty("userName") String userName) {
        this.roomKey = roomKey;
        this.message = message;
        this.userName = userName;
    }
}
