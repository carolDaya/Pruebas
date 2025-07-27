package com.example.login.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.login.data.dao.UserDao;
import com.example.login.data.db.AppDataBase; // Corregido el import a AppDatabase
import com.example.login.data.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // Importar para operaciones en segundo plano

public class AuthManager {

    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_LOGGED_IN_USER_PHONE = "loggedInUserPhone";
    private SharedPreferences sharedPreferences;
    private UserDao userDao;
    private ExecutorService executorService; // Pool de hilos para operaciones de fondo

    // *******************************************************************
    // *** INTERFACES DE CALLBACKS (Buenas Prácticas para Asincronía) ***
    // *******************************************************************

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface LoginCallback {
        void onSuccess(String phoneNumber);
        void onFailure(String errorMessage);
    }

    public interface RegistrationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface PhoneNumberCheckCallback {
        void onResult(boolean isRegistered);
        void onFailure(String errorMessage);
    }

    public interface PasswordUpdateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // *******************************************************************
    // *** CONSTRUCTOR Y MÉTODOS EXISTENTES (Modificados para Callbacks) ***
    // *******************************************************************

    public AuthManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.userDao = AppDataBase.getDatabase(context).userDao(); // Usar AppDatabase
        this.executorService = Executors.newSingleThreadExecutor(); // Inicializar el pool de hilos
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("AuthManager", "Error hashing password", e);
            return null;
        }
    }

    // --- Métodos de Registro (Ahora con Callback) ---
    public void registerUser(String name, String phoneNumber, String password, RegistrationCallback callback) {
        executorService.execute(() -> {
            try {
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear la contraseña.");
                    return;
                }

                User existingUser = userDao.getUserByPhoneNumber(phoneNumber);
                if (existingUser != null) {
                    Log.d("AuthManager", "User with phone number " + phoneNumber + " already exists.");
                    callback.onFailure("El número de teléfono ya está registrado.");
                } else {
                    User newUser = new User(name, phoneNumber, hashedPassword);
                    userDao.insertUser(newUser);
                    Log.d("AuthManager", "User registered successfully: " + phoneNumber);
                    callback.onSuccess();
                }
            } catch (Exception e) {
                Log.e("AuthManager", "Error registering user: " + e.getMessage(), e);
                callback.onFailure("Error en el registro: " + e.getMessage());
            }
        });
    }

    // --- Métodos de Login (Ahora con Callback) ---
    public void loginUser(String phoneNumber, String password, LoginCallback callback) {
        executorService.execute(() -> {
            try {
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear la contraseña.");
                    return;
                }

                User foundUser = userDao.getUserByCredentials(phoneNumber, hashedPassword);
                if (foundUser != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_LOGGED_IN_USER_PHONE, phoneNumber);
                    editor.apply();
                    Log.d("AuthManager", "Login successful for user: " + phoneNumber);
                    callback.onSuccess(phoneNumber);
                } else {
                    Log.d("AuthManager", "Login failed: Invalid credentials for " + phoneNumber);
                    callback.onFailure("Credenciales inválidas.");
                }
            } catch (Exception e) {
                Log.e("AuthManager", "Error during login: " + e.getMessage(), e);
                callback.onFailure("Error durante el inicio de sesión: " + e.getMessage());
            }
        });
    }

    // --- Métodos para verificar estado de login (sin cambios, ya que son síncronos con SharedPreferences) ---
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_LOGGED_IN_USER_PHONE);
    }

    public String getLoggedInUserPhoneNumber() {
        return sharedPreferences.getString(KEY_LOGGED_IN_USER_PHONE, null);
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_USER_PHONE);
        editor.apply();
        Log.d("AuthManager", "User logged out.");
    }

    // *******************************************************************
    // *** NUEVOS MÉTODOS PARA EL FLUJO DE RECUPERACIÓN DE CONTRASEÑA ***
    // *** (Ahora con Callbacks) ***
    // *******************************************************************

    /**
     * Verifica si un número de teléfono existe en la base de datos.
     * @param phoneNumber El número de teléfono a verificar.
     * @param callback El callback para notificar el resultado.
     */
    public void isPhoneNumberRegistered(String phoneNumber, PhoneNumberCheckCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByPhoneNumber(phoneNumber);
                callback.onResult(user != null);
            } catch (Exception e) {
                Log.e("AuthManager", "Error checking phone number registration: " + e.getMessage(), e);
                callback.onFailure("Error al verificar el número de teléfono.");
            }
        });
    }

    /**
     * Actualiza la contraseña de un usuario dado su número de teléfono.
     * @param phoneNumber El número de teléfono del usuario cuya contraseña se actualizará.
     * @param newPassword La nueva contraseña (se hasheará antes de guardarse).
     * @param callback El callback para notificar el resultado.
     */
    public void updatePassword(String phoneNumber, String newPassword, PasswordUpdateCallback callback) {
        executorService.execute(() -> {
            try {
                String hashedPassword = hashPassword(newPassword);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear la nueva contraseña.");
                    return;
                }

                User userToUpdate = userDao.getUserByPhoneNumber(phoneNumber);
                if (userToUpdate != null) {
                    userToUpdate.setPasswordHash(hashedPassword); // Asegúrate de usar setPasswordHash
                    userDao.updateUser(userToUpdate);
                    Log.d("AuthManager", "Password updated successfully for phone: " + phoneNumber);
                    callback.onSuccess();
                } else {
                    Log.d("AuthManager", "User not found for password update: " + phoneNumber);
                    callback.onFailure("Usuario no encontrado para actualizar contraseña.");
                }
            } catch (Exception e) {
                Log.e("AuthManager", "Error updating password for phone: " + phoneNumber, e);
                callback.onFailure("Error al actualizar la contraseña: " + e.getMessage());
            }
        });
    }
}