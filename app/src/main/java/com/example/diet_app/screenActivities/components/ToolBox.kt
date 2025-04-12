package com.example.diet_app.screenActivities.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.R


@Composable
fun ToolBox(
    // navController: NavController,
    // currentScreen: String,
    // onScreenSelected: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
)   {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(bottom = 20.dp)
                .drawBehind {
                    val offsetY = -10.dp.toPx()
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, offsetY),
                        end = Offset(size.width, offsetY),
                        strokeWidth = 2.dp.toPx()
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                ToolBoxItem(
                    painterResource(id = R.drawable.home_symbol),
                    "Home"
                    //currentScreen,
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.meals_symbol),
                    "Home"
                    //currentScreen,
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.book_symbol),
                    "Home"
                    //currentScreen,
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.user_symbol),
                    "Home"
                    //currentScreen,
                    // onScreenSelected,
                )
            }
        }

}

@Composable
fun ToolBoxItem(
    painter: Painter,
    screen: String,
    //currentScreen: String,
    // navController: NavController,
    // onScreenSelected: (String) -> Unit,
) {
    val isSelected = true
    val colorFilter = if (isSelected) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)

    Image(
        painter = painter,
        contentDescription = screen,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .size(40.dp)
            .clickable {
                //onScreenSelected(screen)
                // navController.navigate(screen)
            },
        colorFilter = colorFilter
    )
}

