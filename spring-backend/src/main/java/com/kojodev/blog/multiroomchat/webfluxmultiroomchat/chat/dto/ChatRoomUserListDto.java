package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.domain.User;
import io.vavr.collection.Set;

public class ChatRoomUserListDto {

    public final String roomKey;
    public final Set<User> users;

    @JsonCreator
    public ChatRoomUserListDto(@JsonProperty("roomKey") String roomKey,
                               @JsonProperty("users") Set<User> users) {
        this.roomKey = roomKey;
        this.users = users;
    }
}
