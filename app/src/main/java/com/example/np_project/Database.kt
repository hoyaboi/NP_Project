package com.example.np_project

data class User(
    val uid: String,
    val email: String,
    val name: String
)
data class ChatRoom(
    var rID: String? = null,
    var roomName: String? = null,
    var participants: HashMap<String, Boolean> = HashMap(), // 참여자 UID와 참여 여부
    var messages: ArrayList<Message>? = ArrayList() // 메시지 목록
)

data class Message(
    var uid: String? = null,
    var message: String? = null,
    var timestamp: Long? = null
)