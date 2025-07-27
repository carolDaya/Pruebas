package com.example.login.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextPassword, editTextConfirmPassword;
    private CheckBox checkboxTerms;
    private Button buttonRegister;
    private TextView textViewLoginLink;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.edit_text_name);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        buttonRegister = findViewById(R.id.button_register);
        textViewLoginLink = findViewById(R.id.text_view_login_link);

        authManager = new AuthManager(this);

        buttonRegister.setOnClickListener(v -> {
            attemptRegistration();
        });

        textViewLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegistration() {
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Debes aceptar los Términos y Condiciones.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar a AuthManager con el callback
        authManager.registerUser(name, phoneNumber, password, new AuthManager.RegistrationCallback() {
            @Override
            public void onSuccess() {
                // Ejecutar en el hilo principal (UI Thread)
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso. ¡Inicia sesión!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Ejecutar en el hilo principal (UI Thread)
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}