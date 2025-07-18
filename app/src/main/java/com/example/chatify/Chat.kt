package com.example.chatify

import android.graphics.Bitmap

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

//val dummyChats = listOf(
//    Chat(
//        name = "Shruti Patil",
//        message = "Hey! Are we meeting today?",
//        time = "12:45 PM",
//        image = R.drawable.profile_placeholder
//    ),
//    Chat(
//        name = "Rahul Mehta",
//        message = "I'll send you the files by tonight.",
//        time = "11:30 AM",
//        image = R.drawable.profile_placeholder
//    ),
//    Chat(
//        name = "Priya Desai",
//        message = "Thanks for your help!",
//        time = "10:12 AM",
//        image = R.drawable.profile_placeholder
//    ),
//    Chat(
//        name = "Aakash Jain",
//        message = "Call me when you're free.",
//        time = "Yesterday",
//        image = R.drawable.profile_placeholder
//    ),
//    Chat(
//        name = "Sneha Kulkarni",
//        message = "Happy Birthday!! 🎉",
//        time = "Monday",
//        image = R.drawable.profile_placeholder
//    ),
//    Chat(
//        name = "Karan Verma",
//        message = "Meeting got cancelled.",
//        time = "Sunday",
//        image = R.drawable.profile_placeholder
//    )
//)


