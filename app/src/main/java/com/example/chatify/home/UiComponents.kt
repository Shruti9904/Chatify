package com.example.chatify.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chatify.ui.theme.DeepLavender
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.LightLavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import com.example.chatify.ui.theme.SoftPeach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopWhatsappBar(
    currentTab: BottomTab,
    onSearch: (String) -> Unit
) {
    var shouldShowSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth().height(100.dp)
            .background(SoftLilac)
    ) {
        if (shouldShowSearchBar && currentTab == BottomTab.Chats) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    onSearch(searchQuery)
                },
                active = true,
                onActiveChange = { shouldShowSearchBar = it },
                placeholder = {
                    Text("Search...", color = Color.Gray)
                },
                leadingIcon = {
                    IconButton(onClick = {
                        shouldShowSearchBar = false
                        searchQuery = ""
                        onSearch("") // Optional: Reset search results
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = SoftLilac,
                    dividerColor = Color.DarkGray,
                    inputFieldColors = SearchBarDefaults.inputFieldColors(
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )
            ) {}
        } else {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            BottomTab.Chats -> "Chatify"
                            BottomTab.Profile -> "Profile"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Lavender
                    )
                },
                actions = {
                    if (currentTab == BottomTab.Chats) {
                        IconButton(onClick = {
                            shouldShowSearchBar = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = RichCharcoal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftLilac
                )
            )
        }

        if (!shouldShowSearchBar) {
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 1.dp
            )
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopWhatsappBar(currentTab: BottomTab,onSearch:(String)->Unit) {
//    var shouldShowSearchBar by remember { mutableStateOf(false) }
//    var searchQuery by remember { mutableStateOf("") }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp)
//            .background(SoftLilac)
//    ) {
//        if (shouldShowSearchBar) {
//            SearchBar(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                query = searchQuery,
//                onSearch = {
//                    onSearch(searchQuery)
//                },
//                onQueryChange = { searchQuery = it },
//                active = true,
//                onActiveChange = { shouldShowSearchBar = it },
//                placeholder = {
//                    Text("Search...", color = Color.Gray)
//                },
//                leadingIcon = {
//                    if(currentTab==BottomTab.Chats){
//                        IconButton(onClick = { shouldShowSearchBar = false }) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                contentDescription = "Back"
//                            )
//                        }
//                    }
//                },
//                shape = RoundedCornerShape(14.dp),
////                colors = SearchBarDefaults.colors(containerColor = Color.Black.copy(alpha = 0.1f)),
//            ) { }
//        } else {
//            Column {
//                TopAppBar(
//                    title = {
//                        val titleText = when (currentTab) {
//                            BottomTab.Chats-> "Chatify"
//                            BottomTab.Profile -> "Profile"
//                        }
//                        if (currentTab == BottomTab.Chats) {
//                            Text(
//                                text = titleText,
//                                fontWeight = FontWeight.Bold,
//                                color = Lavender,
//                                fontSize = 24.sp
//                            )
//                        } else {
//                            Text(text = titleText, fontSize = 24.sp, color = Lavender)
//                        }
//                    },
//                    actions = {
//                        if(currentTab==BottomTab.Chats){
//                            IconButton(
//                                onClick = { shouldShowSearchBar = true },
//                                modifier = Modifier
//                                    .size(32.dp)
//                                    .padding(end = 8.dp)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Search,
//                                    contentDescription = "Search",
//                                    tint = RichCharcoal
//                                )
//                            }
//                        }
//
//
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = SoftLilac
//                    )
//                )
//                HorizontalDivider(color = Color.Gray.copy(alpha = 0.4f), thickness = 1.dp)
//            }
//        }
//    }
//}

@Composable
fun FloatingButton(onClick:()->Unit) {
    FloatingActionButton(
        onClick = { onClick()},
        containerColor = DeepLavender,
        contentColor = Color.White,
        shape = CircleShape

    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = SoftPeach
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomTab.Chats,
        BottomTab.Profile
    )

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        HorizontalDivider(color = LightLavender, thickness = 0.4.dp)
        NavigationBar(containerColor = SoftLilac)  {
            items.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == tab.route) {
                                tab.selectedIcon
                            } else {
                                tab.defaultIcon
                            },
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    selected = currentRoute == tab.route,
                    onClick = {
                        if (currentRoute != tab.route) {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(SoftLilac),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepLavender,
                        unselectedIconColor = Lavender,
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Lavender
                    ),
                )
            }
        }
    }
}
