package com.example.diet_app

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseManager(private val context: Context) {

    companion object {
        private const val DATABASE_NAME = "DietApp.db"
    }

    // Ruta interna donde se copiará la base de datos
    private val databasePath: String
        get() = context.getDatabasePath(DATABASE_NAME).path

    init {
        copyDatabaseFromAssets() // Copia la base de datos al almacenamiento interno si no existe
    }

    private fun copyDatabaseFromAssets() {
        val dbFile = File(databasePath)

        if (!dbFile.exists()) {
            try {
                // Crear directorio para bases de datos internas si no existe
                dbFile.parentFile?.mkdirs()

                // Copiar la base de datos desde assets
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }
                        outputStream.flush()
                    }
                }

                Log.d("DatabaseManager", "Base de datos copiada exitosamente a: $databasePath")
            } catch (e: IOException) {
                Log.e("DatabaseManager", "Error al copiar la base de datos: ${e.message}")
                throw RuntimeException("Error copiando la base de datos", e)
            }
        } else {
            Log.d("DatabaseManager", "La base de datos ya existe en: $databasePath")
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    // Autenticar un usuario con email y contraseña
    fun authenticateUser(email: String, password: String): Boolean {
        val query = "SELECT * FROM Users WHERE email = ? AND password = ?"
        var isAuthenticated = false

        try {
            val database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
            val cursor: Cursor = database.rawQuery(query, arrayOf(email, password))
            if (cursor.moveToFirst()) {
                isAuthenticated = true // Usuario encontrado
            }
            cursor.close()
            database.close()
        } catch (e: Exception) {
            Log.e("DatabaseManager", "Error en la autenticación: ${e.message}")
        }

        return isAuthenticated
    }
}
