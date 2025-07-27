package com.example.login.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.login.data.dao.UserDao;
import com.example.login.data.model.User;

// Define las entidades (tablas) y la versión de la base de datos
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    // Método abstracto para obtener el DAO
    public abstract UserDao userDao();

    // Implementación Singleton para asegurar una única instancia de la DB
    private static volatile AppDataBase INSTANCE;
    private static final String DATABASE_NAME = "login_database"; // Nombre del archivo de la DB

    public static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // Útil para desarrollo: borra y recrea la DB si el esquema cambia. ¡NO USAR EN PRODUCCIÓN CON DATOS IMPORTANTES!
                            .allowMainThreadQueries() // <<<<<<<<< TEMPORAL: PERMITE QUERIES EN HILO PRINCIPAL (QUITAR EN PRODUCCIÓN)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}