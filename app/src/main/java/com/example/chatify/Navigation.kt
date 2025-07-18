package com.example.chatify

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatify.HomeScreen
import com.example.chatify.home.ChatDetailScreen

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

