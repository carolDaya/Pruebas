package com.example.login.ui.passwordrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.ui.login.LoginActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputPhoneForgot;
    private Button buttonResetPassword;
    private TextView textViewBackToLogin;
    private ImageView closeButton;
    private ImageView arrowBackToLogin;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicializar vistas
        inputPhoneForgot = findViewById(R.id.input_phone_forgot);
        buttonResetPassword = findViewById(R.id.button_reset_password);
        textViewBackToLogin = findViewById(R.id.text_view_back_to_login);
        closeButton = findViewById(R.id.close_button);

        authManager = new AuthManager(this);

        // Listener para el botón "Restablecer contraseña"
        buttonResetPassword.setOnClickListener(v -> {
            attemptPasswordReset();
        });

        // Listener para el texto y la flecha "Volver a iniciar sesión"
        textViewBackToLogin.setOnClickListener(v -> {
            navigateToLogin();
        });

        // Listener para el botón de cerrar (X)
        closeButton.setOnClickListener(v -> {
            finish(); // Cierra esta actividad y vuelve a la anterior (Login)
        });
    }

    private void attemptPasswordReset() {
        String phoneNumber = inputPhoneForgot.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Por favor, ingresa tu número de celular.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamada asíncrona a AuthManager.isPhoneNumberRegistered con un callback
        authManager.isPhoneNumberRegistered(phoneNumber, new AuthManager.PhoneNumberCheckCallback() {
            @Override
            public void onResult(boolean isRegistered) {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    if (isRegistered) {
                        Toast.makeText(ForgotPasswordActivity.this, "Número de celular verificado. Procede a restablecer tu contraseña.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("phone_number", phoneNumber);
                        startActivity(intent);
                        finish(); // Finalizar esta actividad para que no se pueda volver
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Número de celular no registrado.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Error al verificar número: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}