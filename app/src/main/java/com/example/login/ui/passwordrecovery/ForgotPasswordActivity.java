package com.example.login.ui.passwordrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.databinding.ActivityForgotPasswordBinding;
import com.example.login.ui.login.LoginActivity;

/**
 * ForgotPasswordActivity
 * Permite al usuario solicitar restablecimiento de contraseña:
 * -Valida número.
 * -Consulta AuthManager.
 * -Navega a ResetPasswordActivity si el número es válido.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        binding.buttonResetPassword.setOnClickListener(v -> attemptPasswordReset());
        binding.textViewBackToLogin.setOnClickListener(v -> navigateToLogin());
        binding.closeButton.setOnClickListener(v -> finish());
    }

    /**
     * Valida que el número no esté vacío y lo verifica en AuthManager.
     */
    private void attemptPasswordReset() {
        String phoneNumber = binding.inputPhoneForgot.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            showToast(R.string.error_empty_phone);
            return;
        }

        // Verificar si el número está registrado
        authManager.isPhoneNumberRegistered(phoneNumber, new AuthManager.PhoneNumberCheckCallback() {
            @Override
            public void onResult(boolean isRegistered) {
                runOnUiThread(() -> {
                    if (isRegistered) {
                        showToast(R.string.phone_verified);
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("phone_number", phoneNumber);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast(R.string.phone_not_registered);
                    }
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
    /**
     * Navega de vuelta a la LoginActivity cerrando ForgotPasswordActivity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void showToast(int stringRes) {
        Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
