package com.example.chatify

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatify.auth.AuthScreen
import com.example.chatify.auth.UserRegistrationScreen
import com.example.chatify.home.chats.ChatDetailScreen

@Composable
fun Navigation(innerPaddingValues: PaddingValues){
    val navController = rememberNavController()

    NavHost(
        navController, startDestination = Screen.SplashScreen.route,
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController,innerPaddingValues=innerPaddingValues)
        }

        composable(Screen.AuthScreen.route) {
            AuthScreen(navController,innerPaddingValues=innerPaddingValues)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.UserRegistrationScreen.route){
            UserRegistrationScreen(navController = navController,innerPaddingValues=innerPaddingValues)
        }
        composable(Screen.ContactsScreen.route){
            ContactsScreen(navController)
        }
        composable("chat-detail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ChatDetailScreen(
                userId = userId,
                navController = navController,
                innerPaddingValues = innerPaddingValues
            )
        }
    }
}

