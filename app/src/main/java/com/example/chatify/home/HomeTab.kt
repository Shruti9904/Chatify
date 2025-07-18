package com.example.chatify.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(val title: String, val icon: ImageVector) {
    data object Chats : BottomTab("Chats", Icons.Outlined.Chat)
    data object Profile : BottomTab("Profile", Icons.Outlined.Person)
}