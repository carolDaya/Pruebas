package com.example.login.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.databinding.ActivityRegisterBinding;
import com.example.login.ui.login.LoginActivity;

/**
 * RegisterActivity
 * Esta actividad permite registrar nuevos usuarios:
 * - Configura la UI con ViewBinding.
 * - Valida campos.
 * - Usa AuthManager para registrar en backend.
 * - Redirige a Login en éxito.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;  // ViewBinding para acceso a vistas
    private AuthManager authManager;          // Lógica de autenticación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa binding y asigna layout
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa AuthManager
        authManager = new AuthManager(this);

        // Configura listener para registrar
        binding.buttonRegister.setOnClickListener(v -> attemptRegistration());

        // Configura listener para ir a login
        binding.textViewLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Ejecuta la validación de campos y, si es válido, registra al usuario.
     */
    private void attemptRegistration() {
        String name = binding.editTextName.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        if (fieldsAreEmpty(name, phone, password, confirmPassword)) {
            showToast(R.string.error_empty_fields);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast(R.string.error_password_mismatch);
            return;
        }

        if (password.length() < 6) {
            showToast(R.string.error_password_length);
            return;
        }

        if (!binding.checkboxTerms.isChecked()) {
            showToast(R.string.error_terms_required);
            return;
        }

        // Llama a AuthManager para registrar
        authManager.registerUser(name, phone, password, new AuthManager.RegistrationCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast(R.string.register_success);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> showToast("Error: " + errorMessage));
            }
        });
    }

    private boolean fieldsAreEmpty(String... fields) {
        for (String f : fields) if (f.isEmpty()) return true;
        return false;
    }

    private void showToast(int messageRes) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
