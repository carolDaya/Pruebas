package com.example.login.ui.login;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.login.databinding.ActivityLoginBinding;
import com.example.login.ui.passwordrecovery.ForgotPasswordActivity;
import com.example.login.ui.register.RegisterActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * Clase auxiliar para configurar los listeners de la UI de LoginActivity usando ViewBinding.
 */
public class LoginListenersSetup {

    private final LoginActivity activity;
    private final ActivityLoginBinding binding;
    private final BottomSheetBehavior<View> bottomSheetBehavior;

    public LoginListenersSetup(LoginActivity activity, ActivityLoginBinding binding, BottomSheetBehavior<View> bottomSheetBehavior) {
        this.activity = activity;
        this.binding = binding;
        this.bottomSheetBehavior = bottomSheetBehavior;
    }

    public void setupListeners() {

        // Crear TextWatcher para los campos de entrada
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                activity.checkFieldsAndAdjustBottomSheet();
            }
        };

        binding.inputPhone.addTextChangedListener(textWatcher);
        binding.inputPassword.addTextChangedListener(textWatcher);

        // Botón de acceso
        binding.myButton.setOnClickListener(v -> activity.attemptLogin());

        // Link para registrarse
        binding.newUserSignup.setOnClickListener(v -> {
            Intent intent = new Intent(activity, RegisterActivity.class);
            activity.startActivity(intent);
        });

        // Link para recuperar contraseña
        binding.forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ForgotPasswordActivity.class);
            activity.startActivity(intent);
        });

        // Callback para el BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.myButton.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (TextUtils.isEmpty(binding.inputPhone.getText().toString().trim()) ||
                            TextUtils.isEmpty(binding.inputPassword.getText().toString().trim())) {
                        binding.myButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Opcional: manejar la animación de arrastre
            }
        });
    }
}
