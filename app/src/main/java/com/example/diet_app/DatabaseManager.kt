import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseManager(private val context: Context) {

    companion object {
        private const val DATABASE_NAME = "DietApp.db"
    }

    private val databasePath: String
        get() = context.getDatabasePath(DATABASE_NAME).path

    init {
        copyDatabaseFromAssets()
    }

    private fun copyDatabaseFromAssets() {
        val dbFile = File(databasePath)

        if (!dbFile.exists()) {
            try {
                dbFile.parentFile?.mkdirs()
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
                Log.e("DatabaseManager", "Error al copiar la base de datos desde assets: ${e.message}")
                // throw RuntimeException("Error copiando la base de datos", e)
            }
        } else {
            Log.d("DatabaseManager", "La base de datos ya existe en: $databasePath")
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
    }
}
