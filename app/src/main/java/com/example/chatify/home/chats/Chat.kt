package com.example.chatify.home.chats

data class Chat(
    val userId: String? = null,
    val name: String? = null,
    val lastMessage: String? = null,
    val lastMessageSenderId: String? = null,
    val time: String? = null,
    val phoneNo: String? = null,
    val profile: String? = null,
    val status: MessageStatus = MessageStatus.SENT
)


