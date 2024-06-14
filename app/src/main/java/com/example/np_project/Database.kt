package com.example.np_project

// 사용자 정보를 나타내는 데이터 클래스
data class User(
    val uid: String,    // 사용자 고유 ID
    val email: String,  // 사용자 이메일
    val name: String    // 사용자 이름
)

// 채팅방 정보를 나타내는 데이터 클래스
data class ChatRoom(
    var rID: String? = null,                       // 채팅방 ID
    var roomName: String? = null,                  // 채팅방 이름
    var participants: HashMap<String, Boolean> = HashMap(), // 참여자 UID와 참여 여부
    var messages: HashMap<String, Message>? = HashMap()     // 메시지 목록
)

// 메시지 정보를 나타내는 데이터 클래스
data class Message(
    var uid: String? = null,       // 메시지를 보낸 사용자 UID
    var message: String? = null,   // 메시지 내용
    var timestamp: Long? = null    // 메시지가 전송된 시간
)

// 채팅 항목을 나타내는 시일드 클래스
sealed class ChatItem {
    // 메시지 항목을 나타내는 데이터 클래스
    data class MessageItem(val message: Message) : ChatItem()

    // 날짜 항목을 나타내는 데이터 클래스
    data class DateItem(val date: String) : ChatItem()
}
