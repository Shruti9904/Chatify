package com.example.chatify.home.chats

data class Message(
    val senderId : String? = null,
    val receiverId :String? = null,
    val senderPhoneNumber: String? = null,
    val receiverPhoneNumber: String? = null,
    val message :String? = null,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp : Long = 0L
)
