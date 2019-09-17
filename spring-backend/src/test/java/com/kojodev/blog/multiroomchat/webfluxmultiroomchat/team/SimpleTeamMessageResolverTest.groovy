package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.team

import spock.lang.Specification

class SimpleTeamMessageResolverTest extends Specification {

    def "get team from message"() {
        given: "join message"
        def SimpleTeamMessageResolver messageResolver = new SimpleTeamMessageResolver(null, joinMessage)
        when: "getting team color from message"
        def teamColor = messageResolver.teamColorFromMessage();
        then: "color should be correct"
        teamColor == expectedColor;
        where:
        joinMessage    | expectedColor
        "join: RED"    | "RED"
        "join: blue"   | "BLUE"
        "join:red"     | "RED"
        "join:   lol " | "BLUE"
        "join:"        | "BLUE"
    }
}
