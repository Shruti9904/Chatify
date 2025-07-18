package com.example.chatify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.ui.theme.Lavender
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: PhoneAuthViewModel = hiltViewModel(),
    innerPaddingValues: PaddingValues
) {
    LaunchedEffect(Unit) {
        delay(3000)
        val isLoggedIn = viewModel.isUserLoggedIn()
        val isSignedIn = viewModel.isSignedIn()

        if(isLoggedIn){
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        }else{
            if(isSignedIn){
                navController.navigate(Screen.UserRegistrationScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }else{
                navController.navigate(Screen.AuthScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender).padding(innerPaddingValues),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.chatify_logo),
            contentDescription = "",modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Crop
        )
    }

}