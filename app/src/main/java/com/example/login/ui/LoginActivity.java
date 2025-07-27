package com.example.login.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull; // Importar para @NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.login.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button accessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Aplica insets para manejar barras del sistema si EdgeToEdge está activo
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los campos de entrada y al botón de acceso.
        inputPhone = findViewById(R.id.input_phone);
        inputPassword = findViewById(R.id.input_password);
        accessButton = findViewById(R.id.myButton);

        // Referencia al BottomSheet.
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Configuración inicial del BottomSheetBehavior:
        // peekHeight: Altura en estado colapsado (suficiente para ver solo la línea).
        // Si tu línea de la imagen es más alta, ajusta este valor.
        bottomSheetBehavior.setPeekHeight(30);

        // Ahora sí permitimos que el usuario lo arrastre libremente
        bottomSheetBehavior.setHideable(false); // Mantenerlo no ocultable completamente con un gesto hacia abajo muy fuerte
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Inicia colapsado.

        // Ocultar el botón "Acceso" al inicio. Se hará visible al expandirse el sheet.
        accessButton.setVisibility(View.GONE);

        // Listener para monitorear cambios en el texto (mientras escriben)
        // Esto es más reactivo que solo al perder el foco.
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No hacemos nada aquí, la lógica de validación la hacemos después de que el texto ha cambiado.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Cada vez que el texto cambia en cualquiera de los campos, validamos
                checkFieldsAndAdjustBottomSheet();
            }
        };

        inputPhone.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);

        // Listener para la acción del botón "Acceso".
        accessButton.setOnClickListener(v -> {
            String phone = inputPhone.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
                // Lógica de acceso real aquí (ej. llamar a una API, verificar credenciales).
                Toast.makeText(this, "Iniciando sesión con " + phone, Toast.LENGTH_SHORT).show();

                // Opcional: Colapsar el BottomSheet y ocultar el botón después de la acción.
                // Esto es común si se quiere "cerrar" la parte de login después de un intento.
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                accessButton.setVisibility(View.GONE);

                // También podrías limpiar los campos si la sesión falló o si se quiere un nuevo intento
                // inputPhone.setText("");
                // inputPassword.setText("");

            } else {
                Toast.makeText(this, "Por favor, completa ambos campos.", Toast.LENGTH_SHORT).show();
            }
        });

        // Opcional: Un listener para el BottomSheetBehavior para controlar la visibilidad del botón
        // Esto es útil si el usuario arrastra el sheet manualmente.
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    accessButton.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    accessButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Puedes usar el slideOffset para hacer animaciones mientras se desliza el sheet.
            }
        });
    }

    /**
     * Verifica si los campos de teléfono y contraseña están llenos.
     * Si ambos están llenos, expande el BottomSheet y muestra el botón.
     * Si alguno está vacío, colapsa el BottomSheet y oculta el botón.
     */
    private void checkFieldsAndAdjustBottomSheet() {
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            // Si ambos campos están llenos y el sheet no está ya expandido, lo expandimos.
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            // La visibilidad del botón se maneja en el onStateChanged del BottomSheetCallback
            // o puedes forzarla aquí si prefieres un control más directo al llenar campos.
            accessButton.setVisibility(View.VISIBLE);
        } else {
            // Si algún campo está vacío y el sheet no está ya colapsado, lo colapsamos.
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            // La visibilidad del botón se maneja en el onStateChanged del BottomSheetCallback
            // o puedes forzarla aquí.
            accessButton.setVisibility(View.GONE);
        }
    }
}