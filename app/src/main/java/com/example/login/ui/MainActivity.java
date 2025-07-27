package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
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

        // Si el usuario no está logueado, redirigir a LoginActivity
        if (!authManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finaliza MainActivity si no está logueado
            return;
        }

        btnLogout.setOnClickListener(v -> {
            authManager.logoutUser();
            Toast.makeText(MainActivity.this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Cierra MainActivity
        });
    }
}