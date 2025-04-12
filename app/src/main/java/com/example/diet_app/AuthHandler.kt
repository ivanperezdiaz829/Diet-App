package com.example.diet_app

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthHandler {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun usuarioActual(): Boolean {
        return auth.currentUser != null
    }

    fun iniciarSesion(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthHandler", "Inicio de sesión exitoso para: $email")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthHandler", "Error de inicio de sesión: ${exception.message}")
                callback(false, exception.message)
            }
    }

    fun registrarUsuario(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthHandler", "Registro exitoso para: $email")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthHandler", "Error de registro: ${exception.message}")
                callback(false, exception.message)
            }
    }

    fun checkDatabaseConnection() {
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

    fun fetchAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FirestoreUsers", "No se encontraron usuarios en la base de datos.")
                } else {
                    for (document in documents) {
                        Log.d("FirestoreUsers", "Usuario ID: ${document.id}, Datos: ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreUsers", "Error al obtener usuarios: ${exception.message}")
            }
    }
}
