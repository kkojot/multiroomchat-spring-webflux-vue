package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.greetings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloMessage {

    public final String name;

    @JsonCreator
    public HelloMessage(
            @JsonProperty("name") String name) {
        this.name = name;
    }
}
