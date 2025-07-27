package com.example.login.ui.passwordrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.ui.login.LoginActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputNewPassword, inputConfirmNewPassword;
    private Button buttonSetNewPassword;
    private ImageView backArrow;

    private String phoneNumber;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Inicializar vistas
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmNewPassword = findViewById(R.id.input_confirm_new_password);
        buttonSetNewPassword = findViewById(R.id.button_set_new_password);
        backArrow = findViewById(R.id.back_arrow);

        authManager = new AuthManager(this);

        // Obtener el número de teléfono pasado de ForgotPasswordActivity
        if (getIntent().getExtras() != null) {
            phoneNumber = getIntent().getStringExtra("phone_number");
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Error: Número de teléfono no disponible para restablecer.", Toast.LENGTH_LONG).show();
                finish(); // Cierra la actividad si no hay número válido
                return;
            }
        } else {
            Toast.makeText(this, "Error: No se ha proporcionado el número de teléfono para restablecer.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Listener para el botón "Establecer Nueva Contraseña"
        buttonSetNewPassword.setOnClickListener(v -> {
            attemptPasswordResetConfirmation();
        });

        // Listener para la flecha de atrás
        backArrow.setOnClickListener(v -> {
            finish(); // Volver a la actividad anterior (ForgotPasswordActivity)
        });
    }

    private void attemptPasswordResetConfirmation() {
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmNewPassword = inputConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "Por favor, ingresa y confirma tu nueva contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamada asíncrona a AuthManager.updatePassword con un callback
        authManager.updatePassword(phoneNumber, newPassword, new AuthManager.PasswordUpdateCallback() {
            @Override
            public void onSuccess() {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(ResetPasswordActivity.this, "Contraseña restablecida exitosamente. ¡Inicia sesión con tu nueva contraseña!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(ResetPasswordActivity.this, "Error al restablecer contraseña: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}