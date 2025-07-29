package com.example.chatify.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.R
import com.example.chatify.Screen
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftGray
import com.example.chatify.ui.theme.SoftPeach

@Composable
fun AuthScreen(
    navController: NavController,
    phoneAuthViewModel: PhoneAuthViewModel = hiltViewModel(),
    innerPaddingValues: PaddingValues
) {
    val authState by phoneAuthViewModel.authState.collectAsState()
    val context = LocalContext.current
    var phoneNo by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        Log.d("Auth state","$authState")
        when (authState) {

            is AuthState.NewRegistration -> {
                navController.popBackStack()
                navController.navigate(Screen.UserRegistrationScreen.route)
            }
            is AuthState.Success -> {
                navController.popBackStack()
                navController.navigate(Screen.HomeScreen.route)
            }

            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }

    when(authState){
        is AuthState.Ideal -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Lavender)
                    .padding(horizontal = 8.dp, vertical = 20.dp).padding(innerPaddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.chatify_logo),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "Enter your Phone Number",
                    color = SoftPeach,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("We will send you the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("6 digit ")
                        }
                        append("verification code")
                    },
                    color = SoftGray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                TextField(
                    value = phoneNo,
                    onValueChange = { phoneNo = it },
                    placeholder = { Text("phone number", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SoftPeach,
                        unfocusedContainerColor = SoftPeach,
                        unfocusedTextColor = RichCharcoal,
                        focusedTextColor = RichCharcoal,
                        focusedLeadingIconColor = Color.Gray,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text="+91 ")
                            VerticalDivider(modifier = Modifier.height(48.dp))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Generate OTP"
                ) {
                    val fullPhoneNo = "+91$phoneNo"
                    if (fullPhoneNo.isEmpty() || phoneNo.length != 10) {
                        Toast.makeText(context, "Please enter valid Phone no", Toast.LENGTH_LONG).show()
                    } else {
                        phoneAuthViewModel.sendOtp(fullPhoneNo, context )
                    }
                }
            }
        }
        is AuthState.CodeSent ->{
            val fullPhoneNo = "+91$phoneNo"
            OtpVerificationScreen(
                phoneNumber = fullPhoneNo,
                onVerifyClick = {
                    phoneAuthViewModel.verifyOtp(it)
                },
                onBackClick = {
                    phoneAuthViewModel.updateAuthState(AuthState.Ideal)
                }
            )
        }
        is AuthState.Loading ->{
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator()
            }
        }

        else->{}

    }
}


@Composable
fun PrimaryButton(text:String,onClick:()->Unit){
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = RichCharcoal,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}