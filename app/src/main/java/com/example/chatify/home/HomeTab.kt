package com.example.chatify.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(
    val route: String,
    val title: String,
    val defaultIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Chats : BottomTab("chats", "Chats", Icons.Outlined.Chat, Icons.Filled.Chat)
    data object Profile : BottomTab("profile", "Profile", Icons.Outlined.Person, Icons.Filled.Person)
}