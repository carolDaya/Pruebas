package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.ui.admin.AdminPanelActivity;
import com.example.login.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnLogout;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnLogout);
        authManager = new AuthManager(this);

        if (!authManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        int userId = authManager.getLoggedInUserId();

        if (userId == 1) {
            //Este es el admin (UID = 1)
            Toast.makeText(this, "Bienvenido administrador", Toast.LENGTH_SHORT).show();
            goToAdminPanel();
        } else {
            // Usuario normal
            Toast.makeText(this, "Bienvenido usuario normal", Toast.LENGTH_SHORT).show();
        }
        btnLogout.setOnClickListener(v -> {
            authManager.logoutUser();
            goToLogin();
        });
    }
    private void goToAdminPanel() {
        Intent intent = new Intent(this, AdminPanelActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}