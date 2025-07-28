package com.example.login.ui.passwordrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.databinding.ActivityResetPasswordBinding;
import com.example.login.ui.login.LoginActivity;

/**
 * ResetPasswordActivity
 * Pantalla para establecer una nueva contraseña:
 * - Recibe número de celular desde ForgotPasswordActivity.
 * - Valida nueva contraseña.
 * - Actualiza usando AuthManager.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;  // ViewBinding
    private String phoneNumber;                    // Número de celular recibido
    private AuthManager authManager;               // Maneja la lógica de auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar binding
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        // Recuperar número de teléfono de ForgotPasswordActivity
        phoneNumber = getIntent().getStringExtra("phone_number");
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("Error: Número de teléfono no disponible.");
            finish();
            return;
        }

        // Listener: Confirmar restablecimiento de contraseña
        binding.buttonSetNewPassword.setOnClickListener(v -> attemptPasswordResetConfirmation());

        // Listener: Flecha atrás
        binding.backArrow.setOnClickListener(v -> finish());
    }

    /**
     * Valida campos y actualiza la contraseña si es correcto.
     */
    private void attemptPasswordResetConfirmation() {
        String newPassword = binding.inputNewPassword.getText().toString().trim();
        String confirmNewPassword = binding.inputConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            showToast(R.string.error_empty_passwords);
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            showToast(R.string.error_password_mismatch);
            return;
        }

        if (newPassword.length() < 6) {
            showToast(R.string.error_password_length);
            return;
        }

        // Llamada a AuthManager para actualizar la contraseña
        authManager.updatePassword(phoneNumber, newPassword, new AuthManager.PasswordUpdateCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast(R.string.password_reset_success);
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    showToast("Error: " + errorMessage);
                });
            }
        });
    }
    private void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
