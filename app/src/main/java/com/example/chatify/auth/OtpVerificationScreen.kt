package com.example.chatify.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftGray
import com.example.chatify.ui.theme.SoftPeach

@Composable
fun OtpVerificationScreen(phoneNumber:String,onVerifyClick: (String) -> Unit,onBackClick:()->Unit){
    Log.d("hii","received phone is $phoneNumber")
    val otpLength = 6
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }


    Scaffold (
        modifier = Modifier
            .fillMaxSize().background(Lavender).padding(12.dp),
        topBar = {
            IconButton(
                onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = RichCharcoal
                )
            }
        },
        containerColor = Lavender
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().padding(it).background(Lavender)
                .padding(horizontal = 8.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OTP Verification",
                color = SoftPeach,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Enter the otp sent to ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(phoneNumber)
                    }
                },
                color = SoftGray,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(otpLength) { index ->
                    TextField(
                        value = otpValues[index],
                        onValueChange = { value ->
                            if (value.length <= 1 && value.all { it.isDigit() }) {
                                otpValues[index] = value
                                if (value.isNotEmpty() && index < otpLength - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .width(42.dp)
                            .height(50.dp)
                            .focusRequester(focusRequesters[index])
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = {
                    val enteredOtp = otpValues.joinToString("")
                    onVerifyClick(enteredOtp)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RichCharcoal,
                    contentColor = Color.White
                ),
                enabled = otpValues.all { it.isNotEmpty() },
            shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(text = "Verify & Continue", style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}