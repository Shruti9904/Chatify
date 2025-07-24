package com.example.chatify

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class MessageStatus{
    SENT,
    DELIVERED,
    VIEWED
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    val currentUserId = auth.currentUser?.uid
    private val usersRef = database.getReference("users")
    private val chatsRef = database.getReference("chats")
    private val messagesRef = database.getReference("messages")

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> = _chatList.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> = _allUsers

    var currentUserProfile by mutableStateOf<UserProfile?>(null)
    var searchedChat by mutableStateOf<Chat?>(null)


    init {
        getAllContacts()
        loadUserChats()
    }

    private fun loadUserChats() {
        if (currentUserId == null) return

        chatsRef.child(currentUserId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = mutableListOf<Chat>()
                    for (child in snapshot.children) {
                        val chat = child.getValue(Chat::class.java)
                        chat?.let {
                            chats.add(it)
                            markMessagesAsDelivered(it.userId)
                        }
                    }
                    _chatList.value = chats
                }

                override fun onCancelled(error: DatabaseError) {
                    _chatList.value = emptyList()
                }
            })
    }

    fun addChat(receiverUserProfile: UserProfile,messageText: String) {
        if(currentUserId==null) return
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val receiverChat = Chat(
            userId = receiverUserProfile.userId,
            name = receiverUserProfile.name,
            profile = receiverUserProfile.profileImage,
            phoneNo = receiverUserProfile.phoneNo,
            lastMessage = messageText,
            lastMessageSenderId = currentUserId,
            time = currentTime
        )

        val receiverId = receiverUserProfile.userId ?: return

        val senderChat = Chat(
            userId = currentUserId,
            name = currentUserProfile?.name,
            phoneNo = currentUserProfile?.phoneNo,
            time = currentTime,
            lastMessage = messageText,
            lastMessageSenderId = currentUserId,
            profile = currentUserProfile?.profileImage
        )
        viewModelScope.launch(Dispatchers.IO) {
            chatsRef.child(currentUserId).child(receiverId).setValue(receiverChat)
            chatsRef.child(receiverId).child(currentUserId).setValue(senderChat)
        }
    }

    // Search a user by phone number
    fun searchUserByPhoneNo(phoneNo: String) {
        if (currentUserId == null) return
        usersRef.orderByChild("phoneNo").equalTo(phoneNo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(FirebaseUserProfile::class.java)
                        searchedChat = user?.let {
                            Chat(
                                name = it.name,
                                phoneNo = it.phoneNo,
                                userId = it.userId,
                                profile = it.profileImage
                            )
                        }
                    } else {
                        searchedChat = null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    searchedChat = null
                }
            })
    }

    fun sendMessage(receiverPhoneNo: String, messageText: String) {
        val senderId = currentUserId ?: return
        val senderPhone = auth.currentUser?.phoneNumber ?: return
        val receiverId = getReceiverIdByPhoneNo(receiverPhoneNo) ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val message = Message(
                senderId = senderId,
                receiverId = receiverId,
                senderPhoneNumber = senderPhone,
                receiverPhoneNumber = receiverPhoneNo,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )

            if (senderId != receiverId) {
                messagesRef.child(receiverId).child(senderId).push().setValue(message)
            }
            messagesRef.child(senderId).child(receiverId).push().setValue(message)
        }
    }


    fun markMessagesAsDelivered(receiverId: String?) {
        val senderId = currentUserId ?: return
        if (receiverId == null) return
        Log.d("Status", "Marking message as delivered for $receiverId")

        messagesRef.child(receiverId).child(senderId).get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val message = child.getValue(Message::class.java)
                if (message != null &&
                    message.status == MessageStatus.SENT
                ) {
                    Log.d("Status", "Reached inside if block for $receiverId")
                    child.ref.child("status").setValue(MessageStatus.DELIVERED)
                }
            }
        }
    }

    private fun markMessagesAsViewed(receiverId: String) {
        val senderId = currentUserId ?: return
        messagesRef.child(receiverId).child(senderId)
            .get().addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val message = child.getValue(Message::class.java)
                    if (message != null) {
                        Log.d("Status", "inside of if block of viewed one with $receiverId")
                        child.ref.child("status").setValue(MessageStatus.VIEWED)
                    }
                }
            }
    }


    // Fetch all messages in a conversation
    fun loadMessages(receiverPhoneNo: String, callback: (List<Message>) -> Unit) {
        val senderId = currentUserId ?: return
        val receiverId = getReceiverIdByPhoneNo(receiverPhoneNo) ?: return
        Log.d("Status", "marking messages as viewed for $receiverId")

        messagesRef.child(senderId).child(receiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    for (child in snapshot.children) {
                        val msg = child.getValue(Message::class.java)
                        msg?.let { messages.add(it) }
                    }
                    messages.sortBy { it.timestamp }
                    callback(messages)

                    markMessagesAsViewed(receiverId)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })

    }

    private fun getAllContacts() {
        viewModelScope.launch {
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<UserProfile>()
                    for (child in snapshot.children) {
                        Log.d("ChatViewModel", "child is $child")
                        val user = child.getValue(FirebaseUserProfile::class.java)
                        user?.toUserProfile()?.let {
                            users.add(it)
                        }

                    }
                    _allUsers.value = users.sortedBy { it.name }
                    Log.d("ChatViewModel", "all contacts are ${_allUsers.value}")
                    currentUserProfile = _allUsers.value.firstOrNull { it.userId == currentUserId }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ChatViewModel", "Failed to load users: ${error.message}")

                }
            })
        }
    }

    fun getUnseenMessageCount(receiverId: String?, onResult: (Int) -> Unit) {
        if (currentUserId == null) return
        if (receiverId == null) return
        var count by mutableStateOf(0)
        messagesRef.child(currentUserId).child(receiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val message = child.getValue(Message::class.java)
                        if (message?.status != MessageStatus.VIEWED && message?.senderId != currentUserId) {
                            count++
                        }
                    }
                    onResult(count)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(0)
                    Log.d("Error", "Cant count unseen messages for $receiverId as ${error.message}")
                }
            })
    }

    private fun getReceiverIdByPhoneNo(receiverPhoneNo: String): String? {
        val receiver = _allUsers.value.firstOrNull { it.phoneNo == receiverPhoneNo }
        return receiver?.userId
    }
}
