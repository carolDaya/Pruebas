package com.example.login.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.data.repository.AuthManager;
import com.example.login.databinding.ActivityAdminPanelBinding;
import com.example.login.ui.login.LoginActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private ActivityAdminPanelBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        // Validar que solo el admin (id = 1) acceda
        int userId = authManager.getLoggedInUserId();
        if (userId != 1) {
            Toast.makeText(this, "Acceso denegado.", Toast.LENGTH_SHORT).show();
            goToLogin();
            return;
        }
        binding.btnLogoutAdmin.setOnClickListener(v -> {
            authManager.logoutUser();
            goToLogin();
        });
    }
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
