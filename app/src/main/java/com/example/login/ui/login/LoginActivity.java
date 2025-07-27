package com.example.login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.ui.MainActivity; // Tu Main Activity (después del login)
import com.example.login.ui.passwordrecovery.ForgotPasswordActivity;
import com.example.login.ui.register.RegisterActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button accessButton;
    private TextView newUserSignup;
    private TextView forgotPasswordText; // Añadido para el enlace de "Olvidaste tu contraseña"

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los campos de entrada y al botón de acceso.
        inputPhone = findViewById(R.id.input_phone);
        inputPassword = findViewById(R.id.input_password);
        accessButton = findViewById(R.id.myButton);
        newUserSignup = findViewById(R.id.new_user_signup);
        forgotPasswordText = findViewById(R.id.forgot_password); // Inicializar el TextView

        // Referencia al BottomSheet.
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Configuración inicial del BottomSheetBehavior:
        bottomSheetBehavior.setPeekHeight(30);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Ocultar el botón "Acceso" al inicio.
        accessButton.setVisibility(View.GONE);

        // Inicializar AuthManager
        authManager = new AuthManager(this);

        // Verificar si el usuario ya está logueado al iniciar la actividad
        if (authManager.isLoggedIn()) {
            navigateToMainScreen();
            return; // Importante para no seguir con el onCreate
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                checkFieldsAndAdjustBottomSheet();
            }
        };

        inputPhone.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);

        // Listener para la acción del botón "Acceso".
        accessButton.setOnClickListener(v -> {
            attemptLogin(); // Llamamos al nuevo método attemptLogin()
        });

        // Listener para el TextView "new_user_signup" (para navegar a RegisterActivity)
        if (newUserSignup != null) {
            newUserSignup.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        // Listener para el TextView "forgot_password" (para navegar a ForgotPasswordActivity)
        if (forgotPasswordText != null) {
            forgotPasswordText.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    accessButton.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (TextUtils.isEmpty(inputPhone.getText().toString().trim()) ||
                            TextUtils.isEmpty(inputPassword.getText().toString().trim())) {
                        accessButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Puedes usar el slideOffset para hacer animaciones mientras se desliza el sheet.
            }
        });
    }

    private void attemptLogin() {
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            inputPhone.setError("El número de celular es requerido.");
            inputPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("La contraseña es requerida.");
            inputPassword.requestFocus();
            return;
        }

        // Llamada asíncrona a AuthManager.loginUser con un callback
        authManager.loginUser(phone, password, new AuthManager.LoginCallback() {
            @Override
            public void onSuccess(String loggedInPhoneNumber) {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show();
                    // Aquí puedes añadir lógica para "recordar sesión" si checkboxRemember está activo
                    navigateToMainScreen();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Asegurarse de que las operaciones de UI se ejecuten en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Error de inicio de sesión: " + errorMessage, Toast.LENGTH_LONG).show();
                    inputPassword.setText(""); // Opcional: limpiar la contraseña
                });
            }
        });
    }

    private void checkFieldsAndAdjustBottomSheet() {
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            accessButton.setVisibility(View.VISIBLE);
        } else {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            accessButton.setVisibility(View.GONE);
        }
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}