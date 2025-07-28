package com.example.login.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.login.data.model.User;
@Dao
/**
 * UserDao define las operaciones de acceso a la base de datos
 * para la tabla 'users'.
 * Room genera automáticamente la implementación en tiempo de compilación.*/
public interface UserDao {
    @Insert
    /**
     * Inserta un nuevo usuario en la tabla 'users'.
     * Room infiere la consulta INSERT automáticamente.
     */
    void insertUser(User user);
    @Query("SELECT * FROM users WHERE phone_number = :phoneNumber LIMIT 1")
    /**
     * Busca un usuario por su número de teléfono.
     * Devuelve null si no lo encuentra.
     */
    User getUserByPhoneNumber(String phoneNumber);
    @Query("SELECT * FROM users WHERE phone_number = :phoneNumber AND password_hash = :passwordHash LIMIT 1")
    /**
     * Verifica credenciales de inicio de sesión.
     * Busca un usuario por teléfono y hash de contraseña.
     * Devuelve null si no hay coincidencia.
     */
    User getUserByCredentials(String phoneNumber, String passwordHash);
    @Update
    /**
     * Actualiza los datos de un usuario.
     * Útil para cambiar la contraseña u otros datos.
     */
    void updateUser(User user);
}
