package com.example.login.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.login.data.dao.UserDao;
import com.example.login.data.model.User;

/**
 * Clase principal de Room Database.
 * Define las entidades que usarás y la versión del esquema.
 */
@Database(
        entities = {User.class}, // que tablas existen
        version = 1,             // Versión actual del esquema. Cambia si modificas tablas.
        exportSchema = false     // Si quieres exportar esquema a carpeta /schemas (útil en producción)
)
public abstract class AppDataBase extends RoomDatabase {
    /**
     * Cada DAO declarado aquí expone operaciones CRUD para una tabla.
     */
    public abstract UserDao userDao();
    /**
     * Singleton para garantizar que exista una sola instancia de la DB en toda la app.
     * Es thread-safe gracias a 'synchronized'.
     */
    private static volatile AppDataBase INSTANCE;
    private static final String DATABASE_NAME = "login_database"; // Archivo físico SQLite

    /**
     * getDatabase(Context)
     * Devuelve la instancia única de AppDataBase.
     * Si no existe, la crea usando Room.
     */
    public static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDataBase.class,
                                    DATABASE_NAME
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
