package com.example.login.data.repositorio;

import com.example.login.data.model.User;
import java.util.ArrayList;
import java.util.List;

public class AuthManager {
    private List<User> users;

    public AuthManager() {
        users = new ArrayList<>();
        // Para pruebas: usuarios quemados
        users.add(new User("1234", "admin"));
        users.add(new User("5555", "clave123"));
    }

    public boolean login(String phone, String password) {
        for (User user : users) {
            if (user.getPhone().equals(phone) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
