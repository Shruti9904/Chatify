package com.example.chatify.home.chats

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatify.home.profile.FirebaseUserProfile
import com.example.chatify.home.profile.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

data class ChatsUiState(
    var chats: List<Chat> = emptyList(),
    var contacts: List<UserProfile> = emptyList(),
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    val currentUserId = auth.currentUser?.uid
    private val usersRef = database.getReference("users")
    private val chatsRef = database.getReference("chats")
    private val messagesRef = database.getReference("messages")

    var uiState by mutableStateOf(ChatsUiState())
    var currentUserProfile by mutableStateOf<UserProfile?>(null)
    private var allChats: List<Chat> = emptyList()
    private var allContacts :List<UserProfile> = emptyList()

    init {
        loadUserChats()
        getAllContacts()
    }

    private fun loadUserChats() {
        if (currentUserId == null) return
        chatsRef.child(currentUserId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = mutableListOf<Chat>()
                    if (!snapshot.exists()) {
                        uiState= uiState.copy(chats = emptyList())
                        return
                    }

                    for (child in snapshot.children) {
                        val chat = child.getValue(Chat::class.java)
                        chat?.let {
                            chats.add(it)
                            it.userId?.let { uid ->
                                markMessagesAsDelivered(uid)
                            }
                        }
                    }
                    allChats = chats
                    uiState= uiState.copy(chats = chats)
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState= uiState.copy(chats = emptyList())
                }
            })
    }

    fun addChat(receiverUserProfile: UserProfile, messageText: String) {
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

        val receiverId = receiverUserProfile.userId

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

    fun searchChats(query: String) {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            uiState = uiState.copy(chats = allChats)
        } else {
            val filtered = allChats.filter {
                it.name?.lowercase()?.contains(trimmed) == true ||
                        it.lastMessage?.lowercase()?.contains(trimmed) == true
            }
            uiState = uiState.copy(chats = filtered)
        }
    }

    fun searchContacts(query: String) {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            uiState = uiState.copy(contacts = allContacts)
        } else {
            val filtered = allContacts.filter {
                it.name.lowercase().contains(trimmed)
            }
            uiState = uiState.copy(contacts = filtered)
        }
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
                    allContacts = users.sortedBy { it.name }
                    uiState= uiState.copy(contacts = users.sortedBy { it.name })
                    currentUserProfile = users.firstOrNull { it.userId == currentUserId }
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
        val receiver = allContacts.firstOrNull { it.phoneNo == receiverPhoneNo }
        return receiver?.userId
    }
}
