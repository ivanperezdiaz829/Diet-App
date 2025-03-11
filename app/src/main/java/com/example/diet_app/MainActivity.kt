package com.example.diet_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.diet_app.ui.theme.DietappTheme
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DietappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        checkDatabaseConnection();
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DietappTheme {
        Greeting("Android")
    }
}


fun checkDatabaseConnection() {
    val db = FirebaseFirestore.getInstance()

    // Intenta realizar una consulta simple a la base de datos
    db.collection("testConnection")
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                Log.d("FirestoreConnection", "Conexión exitosa: la colección está vacía o no existe.")
            } else {
                Log.d("FirestoreConnection", "Conexión exitosa: datos encontrados en la colección.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreConnection", "Error al conectar con la base de datos: ${exception.message}")
        }
}
