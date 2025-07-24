package com.example.chatify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.home.BottomNavigationBarWithPager
import com.example.chatify.home.BottomTab
import com.example.chatify.home.ChatsScreen
import com.example.chatify.home.FloatingButton
import com.example.chatify.home.TopWhatsappBar
import com.example.chatify.home.UpdatesScreen
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
fun HomeScreen(navController: NavController){
    val chatViewModel : ChatViewModel = hiltViewModel()
    val tabs = listOf(
        BottomTab.Chats,
        BottomTab.Profile
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )
    var currentPage by remember { mutableStateOf<BottomTab>(BottomTab.Chats) }

    LaunchedEffect(pagerState.currentPage) {
        currentPage = tabs[pagerState.currentPage]
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBarWithPager(pagerState, tabs)
        },
        topBar = {
            TopWhatsappBar(currentPage, onSearch = { searchQuery ->
                when (currentPage) {
                    is BottomTab.Chats -> {
                        chatViewModel.searchUserByPhoneNo(searchQuery)
                    }

                    else -> {

                    }
                }
            })
        },
        floatingActionButton = {
            if(currentPage== BottomTab.Chats){
                FloatingButton{
                    navController.navigate(Screen.ContactsScreen.route)
                }
            }
        }
    ) { innerPadding->

        HorizontalPager(
            state = pagerState,
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (tabs[page]) {
                BottomTab.Chats-> ChatsScreen(chatViewModel,navController)
                BottomTab.Profile -> ProfileScreen(navController)
            }
        }
    }
}

