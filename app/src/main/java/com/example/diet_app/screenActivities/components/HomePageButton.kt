package com.example.diet_app.screenActivities.components


@Composable
fun ColoredButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick, // Llama a la función onClick pasada como argumento
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(120.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}