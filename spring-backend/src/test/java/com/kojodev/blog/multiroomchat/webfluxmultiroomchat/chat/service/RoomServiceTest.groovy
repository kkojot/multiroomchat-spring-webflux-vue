package com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.service

import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.domain.User
import com.kojodev.blog.multiroomchat.webfluxmultiroomchat.chat.dto.UserRoomKeyDto
import spock.lang.Specification

class RoomServiceTest extends Specification {

    def "created room service should have one default main room"() {
        given: "new room service"
        def roomService = new RoomService();
        when: "get the room list"
        def roomList = roomService.roomList();
        then: "got one default main room"
        roomList.size() == 1;
        roomList.get().key == "main-room";
        !roomList.get().subscribed;
    }

    def "adding new room should work correct"() {
        given: "new room service"
        def roomService = new RoomService();
        when: "adding new room with it's name"
        def room = roomService.addRoom("My new room")
        then: "new room should be created and added to room list"
        room.key == "my-new-room"
        roomService.roomList().size() == 2
        roomService.roomList().contains(room)
    }

    def "new added room should have empty user list"() {
        given: "new room service"
        def roomService = new RoomService();
        when: "taking user list from default room"
        def userList = roomService.usersInChatRoom("main-room")
        then: "user list should be empty and the key should be correct"
        userList.isRight()
        def chatRoomUserList = userList.get()
        chatRoomUserList.users.isEmpty()
        chatRoomUserList.roomKey == "main-room"
    }

    def "adding users to room should work correct"() {
        given: "new room service and users"
        def roomService = new RoomService();
        def user = new UserRoomKeyDto("main-room", "kojot")
        when: "adding user to default room"
        def userList = roomService.addUserToRoom(user);
        then: "user list should have one user"
        userList.isRight();
        def chatRoomUserList = userList.get()
        chatRoomUserList.roomKey == "main-room"
        !chatRoomUserList.users.isEmpty()
        chatRoomUserList.users.contains(new User("kojot"))
    }

    def "removing user from room should work correct"() {
        given: "room service with user"
        def roomService = new RoomService();
        def user = new UserRoomKeyDto("main-room", "kojot")
        roomService.addUserToRoom(user);
        when: "adding user to default room"
        def userList = roomService.removeUserFromRoom(user)
        then: "user list should have no user"
        userList.isRight();
        def chatRoomUserList = userList.get()
        chatRoomUserList.roomKey == "main-room"
        chatRoomUserList.users.isEmpty()
    }

    def "checking present user in a room should work ok"() {
        given: "room service with users"
        def roomService = new RoomService();
        roomService.addUserToRoom(new UserRoomKeyDto("main-room", "kojot"));
        roomService.addUserToRoom(new UserRoomKeyDto("main-room", "tester"));
        roomService.addUserToRoom(new UserRoomKeyDto("stupid-room", "kojot"));
        when: "checking user in the particular room"
        def result = roomService.isUserInRoom(roomKey, userName)
        then: "should return correct value"
        result == expectedValue
        where:
        roomKey        | userName      | expectedValue
        "main-room"    | "kojot"       | true
        "main-room"    | "tester"      | true
        "stupid-room"  | "kojot"       | true
        "stupid-room"  | "tester"      | false
        "stupid-room"  | "stupid-name" | false
        "unknown-room" | "kojot"       | false
    }
}
