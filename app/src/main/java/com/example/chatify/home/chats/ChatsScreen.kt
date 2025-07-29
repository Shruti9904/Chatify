package com.example.chatify.home.chats

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatify.R
import com.example.chatify.Screen
import com.example.chatify.home.profile.decodeBase64ToBitmap
import com.example.chatify.ui.theme.CoralAccent
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import kotlinx.coroutines.delay

@Composable
fun ChatsScreen(viewModel: ChatViewModel, navController: NavController) {
    val uiState = viewModel.uiState
    val chatList = uiState.chats

    val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder).asAndroidBitmap()

    if (chatList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftLilac),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "No chats yet.\n Start a conversation to see it here!",
                textAlign = TextAlign.Center,
                color = RichCharcoal
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftLilac)
        ) {
            items(chatList.size) { index ->
                val chat = chatList[index]
                val profileBitmap =
                    chat.profile?.let { decodeBase64ToBitmap(it) } ?: defaultBitmap
                SingleChatItem(chat, profileBitmap, viewModel) {
                    chat.userId?.let {
                        navController.navigate(Screen.ChatDetailScreen.createRoute(chat.userId))
                    }
                }
            }

        }
    }


}

@Composable
fun SingleChatItem(
    chat: Chat,
    bitmap: Bitmap,
    chatViewModel: ChatViewModel,
    onChatClick: () -> Unit
) {
    val currentUserId = chatViewModel.currentUserId
    val name = if (chat.userId == currentUserId) "${chat.name} (You)" else chat.name ?: ""
    val message = chat.lastMessage ?: ""
    val time = chat.time ?: "--:--"
    var unseenCount by remember { mutableStateOf(0) }

    LaunchedEffect(chat) {
        chatViewModel.getUnseenMessageCount(receiverId = chat.userId) {
            unseenCount = it
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 14.dp)
            .fillMaxWidth()
            .clickable {
                onChatClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(55.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                modifier = Modifier.alpha(0.7f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

        }

        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = time,
                modifier = Modifier
                    .alpha(0.7f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            if (unseenCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(
                            color = CoralAccent,
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unseenCount.toString(),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

        }

    }
}
