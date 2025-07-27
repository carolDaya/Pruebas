package com.example.login.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update; // ¡Importar Update!

import com.example.login.data.model.User;

@Dao // Anotación que marca esto como un DAO de Room
public interface UserDao {
    @Insert // Método para insertar un nuevo usuario
    void insertUser(User user);

    // Método para buscar un usuario por número de teléfono
    @Query("SELECT * FROM users WHERE phone_number = :phoneNumber LIMIT 1")
    User getUserByPhoneNumber(String phoneNumber);

    // Método para verificar credenciales de login
    @Query("SELECT * FROM users WHERE phone_number = :phoneNumber AND password_hash = :passwordHash LIMIT 1")
    User getUserByCredentials(String phoneNumber, String passwordHash);

    @Update // ¡NUEVO MÉTODO! Para actualizar un usuario existente (ej. cambiar contraseña)
    void updateUser(User user);
}