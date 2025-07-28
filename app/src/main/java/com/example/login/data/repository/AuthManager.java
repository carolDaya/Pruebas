package com.example.login.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.login.data.dao.UserDao;
import com.example.login.data.db.AppDataBase;
import com.example.login.data.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthManager {

    // SharedPreferences keys
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_LOGGED_IN_USER_PHONE = "loggedInUserPhone";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    private final SharedPreferences sharedPreferences;
    private final UserDao userDao;
    private final ExecutorService executorService;

    // ---- Interfaces de callbacks ----
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

    // ---- Constructor ----
    public AuthManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.userDao = AppDataBase.getDatabase(context).userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // ---- Hash de contraseña seguro ----
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

    // ---- Registrar usuario ----
    public void registerUser(String name, String phoneNumber, String password, RegistrationCallback callback) {
        executorService.execute(() -> {
            try {
                User existingUser = userDao.getUserByPhoneNumber(phoneNumber);
                if (existingUser != null) {
                    callback.onFailure("Número ya registrado");
                    return;
                }

                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear contraseña");
                    return;
                }

                User newUser = new User(name, phoneNumber, hashedPassword);
                newUser.setName(name);
                newUser.setPhoneNumber(phoneNumber);
                newUser.setPasswordHash(hashedPassword);

                userDao.insertUser(newUser);

                callback.onSuccess();
            } catch (Exception e) {
                callback.onFailure("Error: " + e.getMessage());
            }
        });
    }

    // ---- Login ----
    public void loginUser(String phoneNumber, String password, boolean rememberMe, LoginCallback callback) {
        executorService.execute(() -> {
            try {
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear contraseña");
                    return;
                }

                User foundUser = userDao.getUserByCredentials(phoneNumber, hashedPassword);
                if (foundUser != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_LOGGED_IN_USER_PHONE, phoneNumber);
                    editor.putInt(KEY_LOGGED_IN_USER_ID, foundUser.getUid());
                    editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
                    editor.apply();
                    callback.onSuccess(phoneNumber);
                } else {
                    callback.onFailure("Credenciales inválidas");
                }
            } catch (Exception e) {
                callback.onFailure("Error: " + e.getMessage());
            }
        });
    }

    // ---- Verificar número existente ----
    public void isPhoneNumberRegistered(String phoneNumber, PhoneNumberCheckCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByPhoneNumber(phoneNumber);
                callback.onResult(user != null);
            } catch (Exception e) {
                callback.onFailure("Error: " + e.getMessage());
            }
        });
    }

    // ---- Actualizar contraseña ----
    public void updatePassword(String phoneNumber, String newPassword, PasswordUpdateCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByPhoneNumber(phoneNumber);
                if (user == null) {
                    callback.onFailure("Usuario no encontrado");
                    return;
                }

                String hashedPassword = hashPassword(newPassword);
                if (hashedPassword == null) {
                    callback.onFailure("Error al hashear contraseña");
                    return;
                }

                user.setPasswordHash(hashedPassword);
                userDao.updateUser(user);

                callback.onSuccess();
            } catch (Exception e) {
                callback.onFailure("Error: " + e.getMessage());
            }
        });
    }

    // ---- Estado de sesión ----
    public boolean isLoggedIn() {
        // Un usuario está logeado si tenemos su ID guardado, o su número de teléfono.
        // La opción 'Recuérdame' es para saltarse el login en el futuro, no para definir si está logeado AHORA.
        return sharedPreferences.contains(KEY_LOGGED_IN_USER_ID) || sharedPreferences.contains(KEY_LOGGED_IN_USER_PHONE);
    }

    public int getLoggedInUserId() {
        return sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
    }

    public boolean isRememberMe() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void logoutUser() {
        sharedPreferences.edit().clear().apply();
        Log.d("AuthManager", "User logged out.");
    }
}

