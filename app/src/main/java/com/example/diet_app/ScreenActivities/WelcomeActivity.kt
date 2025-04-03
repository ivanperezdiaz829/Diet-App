package com.example.diet_app.ScreenActivities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.Surface
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import com.example.diet_app.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputDesign() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // StatusBar()
        MainContent(screenWidth, screenHeight)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun StatusBar() {
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm"))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(Color.White)
            .padding(horizontal = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentTime,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            color = Color.Black
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Signal Icon
            Box(
                modifier = Modifier
                    .width(17.dp)
                    .height(11.dp)
                    .background(Color.Black)
            )

            // Wifi Icon
            Box(
                modifier = Modifier
                    .width(15.dp)
                    .height(11.dp)
                    .background(Color.Black)
            )

            // Battery Icon
            // BatteryIcon()
        }
    }
}

@Composable
private fun BatteryIcon() {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(11.dp)
    ) {
        // Battery Border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(1.dp)
        ) {
            // Battery Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight()
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }

        // Battery Tip
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(4.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(1.dp)
                )
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun MainContent(screenWidth: Dp, screenHeight: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight - 42.dp)
            .background(Color(0xFF40B93C))
    ) {
        // Centered Card
        Surface(
            modifier = Modifier
                .width(
                    when {
                        screenWidth > 991.dp -> 307.dp
                        screenWidth > 640.dp -> 280.dp
                        else -> 250.dp
                    }
                )
                .height(
                    when {
                        screenWidth > 991.dp -> 180.dp
                        screenWidth > 640.dp -> 160.dp
                        else -> 140.dp
                    }
                )
                .align(Alignment.Center),
            shape = RoundedCornerShape(50.dp),
            color = Color(0xFFD9D9D9)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // You'll need to add this resource
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(
                            when {
                                screenWidth > 991.dp -> 106.dp
                                screenWidth > 640.dp -> 96.dp
                                else -> 86.dp
                            }
                        )
                        .height(
                            when {
                                screenWidth > 991.dp -> 153.dp
                                screenWidth > 640.dp -> 138.dp
                                else -> 123.dp
                            }
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}