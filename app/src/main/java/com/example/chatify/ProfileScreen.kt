package com.example.chatify

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatify.ui.theme.DeepLavender
import com.example.chatify.ui.theme.DuskyIndigo
import com.example.chatify.ui.theme.DustyViolet
import com.example.chatify.ui.theme.Lavender
import com.example.chatify.ui.theme.RichCharcoal
import com.example.chatify.ui.theme.SoftLilac
import com.example.chatify.ui.theme.TwilightPlum

@Composable
fun ProfileScreen(){
    val phoneAuthViewModel : PhoneAuthViewModel = hiltViewModel()
    var userProfile = phoneAuthViewModel.userProfile

    LaunchedEffect(Unit) {
        userProfile = phoneAuthViewModel.userProfile
    }

    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Lavender)
        }
        return
    }

    val defaultBitmap = ImageBitmap.imageResource(R.drawable.profile_placeholder)
    val profileBitmap = userProfile?.profileImage?.let { phoneAuthViewModel.decodeBase64ToBitmap(it) }

    Column (
        modifier = Modifier.fillMaxSize().background(SoftLilac)
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(DeepLavender),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = profileBitmap?.asImageBitmap() ?: defaultBitmap,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp)
                    .border(2.dp, RichCharcoal, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userProfile?.name ?: "",
                color = Color.White,
//                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                letterSpacing = 1.sp
            )
        }
//        Column(
//            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f).background(CoralAccent),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ){
//            Image(
//                bitmap = profileBitmap?.asImageBitmap() ?: defaultBitmap,
//                contentDescription = null,
//                modifier = Modifier
//                    .clip(CircleShape)
//                    .size(120.dp),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(text = userProfile?.name ?:"",
//                color = DarkBackground,
//                fontWeight = FontWeight.Bold,
//                fontSize = 22.sp,
//                letterSpacing = 1.sp
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(text = userProfile?.phoneNo ?:"",
//                color = DeepLavender,
//                fontSize = 14.sp,
//            )
//        }

    }
}