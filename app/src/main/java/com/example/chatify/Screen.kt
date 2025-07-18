package com.example.chatify

sealed class Screen(val route:String){
    data object SplashScreen:Screen("splash")
    data object AuthScreen: Screen("auth")
    data object UserRegistrationScreen : Screen("profile")
    data object HomeScreen: Screen("home")
    data object ContactsScreen: Screen("contacts")
    data object ChatDetailScreen : Screen("chat-detail/{userId}") {
        fun createRoute(userId: String): String = "chat-detail/$userId"
    }
}