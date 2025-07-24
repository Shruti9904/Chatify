package com.example.chatify

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val currentUserId = viewModel.currentUserId
    val allUsers by viewModel.allUsers.collectAsState()
    val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder).asAndroidBitmap()

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ){
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(vertical = 0.dp)
                        ) {
                            Text(
                                text = "Select contact",
                                fontSize = 16.sp,
                                lineHeight = 18.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "120 contacts",
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                maxLines = 1
                            )

                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                    }
                )
                HorizontalDivider(thickness = 0.3.dp, color = Color.LightGray)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Contacts on WhatsApp",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            items(allUsers.size) { index ->
                val user = allUsers[index]
                val profileBitmap =
                    user.profileImage?.let { decodeBase64ToBitmap(it) } ?: defaultBitmap
                SingleContactItem(user, profileBitmap,currentUserId){
                    navController.navigate(Screen.ChatDetailScreen.createRoute(user.userId))
                }
            }

        }
    }
}

@Composable
fun SingleContactItem(userProfile: UserProfile, bitmap: Bitmap, currentUserId: String?,onClick:()->Unit) {
    val name = if (userProfile.userId == currentUserId) "${userProfile.name} (You)" else userProfile.name

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 16.sp
            )
            Text(
                text = userProfile.status ?: "Hey there! I am sleeping",
                modifier = Modifier.alpha(0.7f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp
            )
        }

    }
}