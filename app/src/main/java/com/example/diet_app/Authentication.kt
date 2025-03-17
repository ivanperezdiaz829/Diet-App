package com.example.diet_app
import com.google.firebase.auth.FirebaseAuth

class Authentication {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Registrar Usuario
    fun registrarUsuario(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Registro exitoso: ${auth.currentUser?.email}")
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Iniciar Sesión
    fun iniciarSesion(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Inicio de sesión exitoso: ${auth.currentUser?.email}")
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Cerrar Sesión
    fun cerrarSesion() {
        auth.signOut()
    }

    // Verificar Usuario Logueado
    fun usuarioActual(): Boolean {
        return auth.currentUser != null
    }
}