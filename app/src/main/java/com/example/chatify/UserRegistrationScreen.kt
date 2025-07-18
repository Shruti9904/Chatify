package com.example.chatify

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.chatify.ui.theme.DustyRose
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.PastelPurple
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import com.example.chatify.ui.theme.SoftPeach

@Composable
fun UserRegistrationScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PhoneAuthViewModel = hiltViewModel()
) {
    val authState = viewModel.authState.collectAsState().value
    var name by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    var uri by remember { mutableStateOf<Uri?>(null) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current
    val preferencesManager = remember {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        PreferencesManager(sharedPreferences)
    }
    val phoneNo = preferencesManager.getString(PHONE_NO,"") ?: ""

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
            profileBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,it)
        }
    )


    LaunchedEffect(authState) {
        when(authState){
            is AuthState.Error -> {
                Toast.makeText(context,authState.message,Toast.LENGTH_LONG).show()
            }
            is AuthState.Success -> {
                navController.popBackStack()
                navController.navigate(Screen.HomeScreen.route)
            }
            else -> {

            }
        }
    }

    Column(
        modifier
            .background(Lavender)
            .padding(innerPaddingValues)
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder)
        Spacer(modifier=Modifier.height(64.dp))
        Box(contentAlignment = Alignment.BottomEnd,modifier = modifier.size(140.dp)){
            Image(
                bitmap = profileBitmap?.asImageBitmap() ?: defaultBitmap,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = modifier
                    .clip(CircleShape)
                    .size(140.dp)
            )
            IconButton(
                onClick = {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier
                    .offset(x = (-4).dp, y = (-2).dp)
                    .size(32.dp)
                    .background(color = PastelPurple, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = RichCharcoal
                )
            }
        }


        Spacer(modifier.height(32.dp))

        Text(text = phoneNo, style = MaterialTheme.typography.titleMedium, color = SoftLilac)

        Spacer(modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = {
                Text("Your name", color = Color.Gray, fontSize = 14.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SoftPeach,
                focusedContainerColor = SoftPeach,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = RichCharcoal,
                unfocusedTextColor = RichCharcoal,
                cursorColor = RichCharcoal
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.border(
                width = 2.dp,
                color = RichCharcoal,
                shape = RoundedCornerShape(12.dp)
            ),
            maxLines = 1
        )

        Spacer(modifier.height(16.dp))
        TextField(
            value = status,
            onValueChange = {
                status = it
            },
            placeholder = {
                Text("About", color = Color.Gray, fontSize = 14.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SoftPeach,
                focusedContainerColor = SoftPeach,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = RichCharcoal,
                unfocusedTextColor = RichCharcoal,
                cursorColor = RichCharcoal
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.border(
                width = 2.dp,
                color = RichCharcoal,
                shape = RoundedCornerShape(12.dp)
            )
        )
        Spacer(modifier.height(32.dp))

        PrimaryButton(text = "SAVE") {
            val bitmap = profileBitmap ?: defaultBitmap.asAndroidBitmap()
            if(name.isEmpty()){
                Toast.makeText(context,"Name can't be empty",Toast.LENGTH_LONG).show()
            }else{
                viewModel.saveUserProfile(name.trim(), bitmap, status.trim())
            }
        }

    }
}