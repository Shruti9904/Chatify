package com.example.chatify.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.ChatViewModel
import com.example.chatify.Message
import com.example.chatify.MessageStatus
import com.example.chatify.R
import com.example.chatify.decodeBase64ToBitmap
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.LightLavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetailScreen(
    userId: String?,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
    innerPaddingValues: PaddingValues
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val user = allUsers.firstOrNull { it.userId == userId }
    val name = user?.name ?: "Unknown"
    val profileString = user?.profileImage
    val receiverPhoneNo = user?.phoneNo

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }

    val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder).asAndroidBitmap()
    val profileBitmap = profileString?.let { decodeBase64ToBitmap(it) } ?: defaultBitmap

    if (receiverPhoneNo != null) {
        viewModel.loadMessages(receiverPhoneNo) {
            messages = it
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Lavender).padding(innerPaddingValues)){
        Scaffold(
            modifier = Modifier
//                .padding(innerPaddingValues)
                .fillMaxWidth(),
            containerColor = Lavender,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Lavender),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Image(
                            bitmap = profileBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SoftLilac)
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.background(Color.Transparent)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = RichCharcoal
                        )
                    }
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type Message", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                            focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                            focusedTextColor = RichCharcoal,
                            unfocusedTextColor = RichCharcoal,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = RichCharcoal
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    receiverPhoneNo = receiverPhoneNo ?: "",
                                    messageText = messageText.trim()
                                )
                                if (user != null) {
                                    viewModel.addChat(user, messageText)
                                }
                                messageText = ""
                            }
                        },
                        modifier = Modifier

                            .clip(CircleShape)
                            .background(Lavender)
                    ) {
                        val icon =
                            if (messageText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->

            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SoftLilac)
                ) {

                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SoftLilac)
                        .padding(paddingValues),
                    reverseLayout = true
                ) {

                    items(messages.reversed()) { message ->
                        ChatBubble(
                            message = message,
                            isSender = message.senderId == viewModel.currentUserId
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

            }

        }
    }



}

@Composable
fun ChatBubble(message: Message, isSender: Boolean) {
    val bubbleColor = if (isSender) Lavender else Color.White
    val textColor = if (isSender) Color.White else Color.Black
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val timeStampAlignment = if (isSender) Alignment.End else Alignment.Start


    val statusIcon = when (message.status) {
        MessageStatus.SENT -> {
            Icons.Default.Done
        }

        else -> {
            Icons.Default.DoneAll
        }
    }
    val statusIconColor = if (message.status == MessageStatus.VIEWED) Color.Blue else Color.Gray
    val currentTime =
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Column {
            Card(
                modifier = Modifier
                    .widthIn(max = 0.8f * screenWidth)
                    .align(timeStampAlignment),
                colors = CardDefaults.cardColors(
                    contentColor = textColor,
                    containerColor = bubbleColor
                )
            ) {
                Text(
                    text = message.message ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(12.dp)
                )

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(timeStampAlignment)
                    .padding(4.dp)
            ) {

                if (isSender) {
                    Icon(
                        imageVector = statusIcon,
                        tint = statusIconColor,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = currentTime,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

            }
        }

    }
}


@Composable
@Preview(showBackground = true)
fun ChatBubblePreview() {
    val message = Message(
        message = "Hi Shruti hi how are",
        timestamp = 1751377717954,
        status = MessageStatus.DELIVERED
    )

    Column(
        modifier = Modifier.background(LightLavender)
    ) {
        ChatBubble(message = message, isSender = true)
        ChatBubble(message = message, isSender = false)
    }
}