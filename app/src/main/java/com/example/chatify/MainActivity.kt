package com.example.chatify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatify.home.BottomNavigationBar
import com.example.chatify.home.BottomTab
import com.example.chatify.home.chats.ChatsScreen
import com.example.chatify.home.FloatingButton
import com.example.chatify.home.TopWhatsappBar
import com.example.chatify.home.chats.ChatViewModel
import com.example.chatify.home.profile.ProfileScreen
import com.example.chatify.ui.theme.ChatifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(innerPadding)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(parentNavController: NavController) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val bottomNavController = rememberNavController()

    val currentRoute by bottomNavController.currentBackStackEntryAsState()
    val currentScreen = currentRoute?.destination?.route

    Scaffold(
        topBar = {
            when (currentScreen) {
                "chats" -> TopWhatsappBar(BottomTab.Chats) {
                    chatViewModel.searchChats(it)
                }
                "profile" -> TopWhatsappBar(BottomTab.Profile){}
                else -> {}
            }
        },
        bottomBar = {
            BottomNavigationBar(bottomNavController)
        },
        floatingActionButton = {
            if (currentScreen == "chats") {
                FloatingButton {
                    parentNavController.navigate(Screen.ContactsScreen.route)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "chats",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chats") {
                ChatsScreen(chatViewModel, parentNavController)
            }
            composable("profile") {
                ProfileScreen(parentNavController)
            }
        }
    }
}


