package com.example.chatify.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatify.R
import com.example.chatify.ui.theme.Lavender

data class Status(
    val name:String,
    val time:String,
    val image: Int
)

val dummyStatusList = listOf(
    Status(
        name = "Shruti Patil",
        time = "Today, 10:00 AM",
        image = R.drawable.woman
    ),
    Status(
        name = "Shahrukh Khan",
        time = "Today, 9:30 AM",
        image = R.drawable.sharukh_khan
    ),
    Status(
        name = "Shraddha Kapoor",
        time = "Today, 8:45 AM",
        image = R.drawable.sharadha_kapoor
    ),
    Status(
        name = "Akshay Kumar",
        time = "Yesterday, 11:00 PM",
        image = R.drawable.akshay_kumar
    ),
    Status(
        name = "Dolly Singh",
        time = "Yesterday, 6:15 PM",
        image = R.drawable.profile_placeholder
    )
)


@Preview(showBackground = true)
@Composable
fun UpdatesScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Status",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyStatusIcon()
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add status",
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Disappears after 24 hours",
                        modifier = Modifier.alpha(0.7f),
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "Recent Updates",
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(top = 12.dp, bottom = 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(dummyStatusList.size){
            SingleStatusItem(status = dummyStatusList[it])
        }

    }
}

@Composable
fun SingleStatusItem(status: Status){
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box{
            Image(
                painter = painterResource(status.image),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = status.name,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 16.sp
            )
            Text(
                text = status.time,
                modifier = Modifier.alpha(0.7f),
                textAlign = TextAlign.Start,
                fontSize = 14.sp
            )
        }

    }
}

@Composable
fun MyStatusIcon() {
    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.ms_dhoni),
            contentDescription = "My Status",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Lavender)
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Status",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

