package com.example.login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.login.R;
import com.example.login.data.repository.AuthManager;
import com.example.login.databinding.ActivityLoginBinding;
import com.example.login.ui.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.introRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setPeekHeight(30);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        authManager = new AuthManager(this);

        // Checa si ya está logueado Y si activó Recuérdame
        if (authManager.isLoggedIn() && authManager.isRememberMe()) {
            navigateToMainScreen();
            return;
        }

        LoginListenersSetup listenersSetup = new LoginListenersSetup(this, binding, bottomSheetBehavior);
        listenersSetup.setupListeners();
    }

    void attemptLogin() {
        String phone = binding.inputPhone.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();
        boolean rememberMe = binding.checkboxRememberMe.isChecked();

        if (TextUtils.isEmpty(phone)) {
            binding.inputPhone.setError(getString(R.string.error_phone_required));
            binding.inputPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.inputPassword.setError(getString(R.string.error_password_required));
            binding.inputPassword.requestFocus();
            return;
        }

        authManager.loginUser(phone, password, rememberMe, new AuthManager.LoginCallback() {
            @Override
            public void onSuccess(String loggedInPhoneNumber) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, getString(R.string.welcome_message), Toast.LENGTH_SHORT).show();
                    navigateToMainScreen();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_error_prefix) + errorMessage, Toast.LENGTH_LONG).show();
                    binding.inputPassword.setText("");
                });
            }
        });
    }

    void checkFieldsAndAdjustBottomSheet() {
        String phone = binding.inputPhone.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            binding.myButton.setVisibility(View.VISIBLE);
        } else {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            binding.myButton.setVisibility(View.GONE);
        }
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

