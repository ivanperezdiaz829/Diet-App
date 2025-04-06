package com.example.diet_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LoginScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF797979))
            .padding(top = 82.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LoginCard()
    }
}
@Preview
@Composable
private fun LoginCard() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .width(342.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // Permitir scroll si el contenido es largo
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header Images
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.healthfoods),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 21.dp, start = 15.dp)
                )
            }

            // Login/Signup Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botón de Log In con borde interno
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(39.dp)  // Ajusta la altura del Box
                        .border(
                            width = 0.1.dp,
                            color = Color(0xFFFBFBFB),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp), // Esto hace que el botón ocupe todo el espacio del Box
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40B93C)),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Default,
                            color = Color.White
                        )
                    }
                }

                // Botón de Sign Up con borde interno
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(39.dp)  // Ajusta la altura del Box
                        .border(
                            width = 0.8.dp,
                            color = Color(0x40000000),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Default,
                            color = Color(0xFF767676)
                        )
                    }
                }
            }

            // Username Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Enter username or email",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Default,
                        color = Color(0xFFC4C4C4)
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Divider(
                    color = Color(0xFFC4C4C4),
                    thickness = 1.dp
                )
            }

            // Password Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {
                Column {
                    Text(
                        text = "Password",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Default,
                            color = Color(0xFFC4C4C4)
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Divider(
                        color = Color(0xFFC4C4C4),
                        thickness = 1.dp
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.show),
                    contentDescription = "Show/Hide Password",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(14.dp)
                )
            }

            // Login Button
            Button(
                onClick = { },
                modifier = Modifier
                    .width(183.dp)
                    .height(39.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40B93C)),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Default,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // OR Divider
            Text(
                text = "OR",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Default,
                    color = Color(0xFF929292)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Social Login Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook Login",
                    modifier = Modifier
                        .size(27.dp)
                        .padding(end = 16.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Login",
                    modifier = Modifier
                        .size(27.dp)
                        .padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}