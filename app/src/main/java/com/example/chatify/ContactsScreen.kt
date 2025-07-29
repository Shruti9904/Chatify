package com.example.chatify

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.home.chats.ChatViewModel
import com.example.chatify.home.profile.UserProfile
import com.example.chatify.home.profile.decodeBase64ToBitmap
import com.example.chatify.ui.theme.DeepLavender
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val currentUserId = viewModel.currentUserId
    val allUsers = viewModel.uiState.contacts
    val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder).asAndroidBitmap()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftLilac),
                verticalArrangement = Arrangement.Center
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchContacts(it) // 🔥 live search on typing
                    },
                    onSearch = {
                        viewModel.searchContacts(searchQuery)
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = {
                        isSearchActive = it
                        if (!it) {
                            searchQuery = ""
                            viewModel.searchContacts("") // 🔁 reset on collapse
                        }
                    },
                    placeholder = {
                        Text("Start a new chat...")
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                                isSearchActive = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = RichCharcoal
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.searchContacts("") // 🔁 clear search
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null,
                                    tint = Color.DarkGray)
                            }
                        } else {
                            IconButton(onClick = {
                                isSearchActive = true
                            }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null,tint= RichCharcoal)
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = SoftLilac,
                        dividerColor = Color.DarkGray,
                        inputFieldColors = SearchBarDefaults.inputFieldColors(
                            focusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            unfocusedTextColor = Color.DarkGray
                        )
                    )
                ) {}

                HorizontalDivider(thickness = 0.3.dp, color = Color.Gray)
            }
        },
        containerColor = SoftLilac
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = buildAnnotatedString {
                        append("People on")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Lavender)) {
                            append(" Chatify")
                        }
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = RichCharcoal
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if(allUsers.isEmpty()){
                item {
                    Box(modifier=Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        Text(text="No contacts found..",color=Color.Gray)
                    }
                }
            }else{
                items(allUsers.size) { index ->
                    val user = allUsers[index]
                    val profileBitmap =
                        user.profileImage?.let { decodeBase64ToBitmap(it) } ?: defaultBitmap
                    SingleContactItem(user, profileBitmap, currentUserId) {
                        navController.navigate(Screen.ChatDetailScreen.createRoute(user.userId))
                    }
                }
            }
        }
    }
}

@Composable
fun SingleContactItem(userProfile: UserProfile, bitmap: Bitmap, currentUserId: String?, onClick:()->Unit) {
    val name = if (userProfile.userId == currentUserId) "${userProfile.name} (You)" else userProfile.name

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp),
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
                fontSize = 16.sp,
                color=Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = userProfile.status ?: "Hey there! I am sleeping",
                modifier = Modifier.alpha(0.7f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }

    }
}