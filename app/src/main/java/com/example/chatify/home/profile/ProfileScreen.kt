package com.example.chatify.home.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatify.Screen
import com.example.chatify.auth.EditableProfileImage
import com.example.chatify.auth.PhoneAuthViewModel
import com.example.chatify.ui.theme.DustyViolet
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import java.util.Locale

@Composable
fun ProfileScreen(navController: NavController) {
    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val phoneAuthViewModel: PhoneAuthViewModel = hiltViewModel()
    var userProfile = userProfileViewModel.userProfile

    var editingLabel by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        userProfile = userProfileViewModel.userProfile
    }

    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Lavender)
        }
        return
    }

    val profileBitmap = userProfile?.profileImage?.let { decodeBase64ToBitmap(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftLilac),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .background(Color.Gray)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            EditableProfileImage(
                initialBitmap = profileBitmap
            ) {bitmap->
                val encodedImage = convertBitmapToBase64(bitmap)
                userProfileViewModel.updateUserProfile(ProfileUpdateType.PROFILE_IMAGE,encodedImage)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card (
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = DustyViolet, contentColor = Color.White),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),

        ){
            ProfileInfoItem(
                label = ProfileUpdateType.NAME.name,
                text = userProfile!!.name,
                isEditing = editingLabel == ProfileUpdateType.NAME.name,
                onEditClick = { editingLabel = ProfileUpdateType.NAME.name },
                onDone = {
                    userProfileViewModel.updateUserProfile(ProfileUpdateType.NAME, it)
                    editingLabel = null
                }
            )

            ProfileInfoItem(
                label = ProfileUpdateType.STATUS.name,
                text = userProfile!!.status ?: "Hey there! I am using Chatify",
                isEditing = editingLabel == ProfileUpdateType.STATUS.name,
                onEditClick = { editingLabel = ProfileUpdateType.STATUS.name },
                onDone = {
                    userProfileViewModel.updateUserProfile(ProfileUpdateType.STATUS, it)
                    editingLabel = null
                }
            )
            ProfileInfoItem(
                label = ProfileUpdateType.PHONE.name,
                text = userProfile!!.phoneNo.take(3) + " " + userProfile!!.phoneNo.substring(3),
                isEditing = false,
                onEditClick = { },
                onDone = {
                    editingLabel = null
                }
            )

            Button(
                onClick = {
                    phoneAuthViewModel.signOut()
                    navController.navigate(Screen.AuthScreen.route) {
                        popUpTo(Screen.HomeScreen.route) {
                            inclusive = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "logout",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Logout", fontSize = 14.sp)

                }

            }
        }


    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    text: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onDone: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(text) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .clickable { onEditClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                TextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .fillMaxWidth(0.9f),
                    label = { Text(text = label) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Lavender,
                        unfocusedLabelColor = Lavender
                    )
                )
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done",
                    tint = Color.Black,
                    modifier = Modifier
                        .clickable {
                            onDone(editedText)
                        }
                        .size(28.dp)
                )
            } else {
                Box(modifier = Modifier.weight(0.3f).padding(start = 8.dp, end = 4.dp)) {
                    Text(
                        text = label.uppercase(Locale.ROOT),
                        color = RichCharcoal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier=Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = text,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = RichCharcoal, thickness = 0.5.dp)
    }
}