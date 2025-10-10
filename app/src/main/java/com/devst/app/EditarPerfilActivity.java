package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class EditarPerfilActivity extends AppCompatActivity {

    // Variables para los campos del formulario
    // IMPORTANTE: Usar EditText, NO TextInputEditText
    private EditText edtNombre;
    private EditText edtRut;
    private EditText edtPassword;
    private Button btnGuardarPerfil;
    private Button btnCancelar;
    private String emailUsuario; // Email recibido del perfil

    // Patrón para validar RUT chileno (formato: 12345678-9)
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d{7,8}-[0-9kK]$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);

        // 1. CONECTAR los elementos del layout con las variables
        edtNombre = findViewById(R.id.edtNombre);
        edtRut = findViewById(R.id.edtRut);
        edtPassword = findViewById(R.id.edtPassword);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnCancelar = findViewById(R.id.btnCancelar);

        // 2. RECIBIR el email del usuario
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email_usuario")) {
            emailUsuario = intent.getStringExtra("email_usuario");
        }

        // 3. CONFIGURAR botón Guardar
        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());

        // 4. CONFIGURAR botón Cancelar
        btnCancelar.setOnClickListener(v -> {
            // Simplemente cerramos esta Activity
            finish();
        });
    }

    /**
     * Método para validar y guardar los cambios del perfil
     */
    private void guardarCambios() {
        // Obtenemos los valores de los campos (con null-safety)
        String nombre = edtNombre.getText() != null ?
                edtNombre.getText().toString().trim() : "";
        String rut = edtRut.getText() != null ?
                edtRut.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ?
                edtPassword.getText().toString() : "";

        // VALIDACIÓN 1: Verificar que el nombre no esté vacío
        if (TextUtils.isEmpty(nombre)) {
            edtNombre.setError("Ingresa tu nombre completo");
            edtNombre.requestFocus();
            return;
        }

        // VALIDACIÓN 2: Verificar que el nombre tenga al menos 3 caracteres
        if (nombre.length() < 3) {
            edtNombre.setError("El nombre debe tener al menos 3 caracteres");
            edtNombre.requestFocus();
            return;
        }

        // VALIDACIÓN 3: Verificar que el RUT no esté vacío
        if (TextUtils.isEmpty(rut)) {
            edtRut.setError("Ingresa tu RUT");
            edtRut.requestFocus();
            return;
        }

        // VALIDACIÓN 4: Verificar formato del RUT (ej: 12345678-9)
        if (!RUT_PATTERN.matcher(rut).matches()) {
            edtRut.setError("Formato inválido. Usa: 12345678-9");
            edtRut.requestFocus();
            return;
        }

        // VALIDACIÓN 5: Verificar que la contraseña no esté vacía
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Ingresa tu contraseña");
            edtPassword.requestFocus();
            return;
        }

        // VALIDACIÓN 6: Verificar longitud mínima de contraseña
        if (password.length() < 6) {
            edtPassword.setError("Mínimo 6 caracteres");
            edtPassword.requestFocus();
            return;
        }

        // Si todas las validaciones pasaron, guardamos los datos
        // NOTA: En una app real, aquí guardarías en una base de datos
        // o enviarías a un servidor. Por ahora solo mostramos un mensaje.

        Toast.makeText(this,
                "✓ Perfil actualizado:\n" +
                        "Nombre: " + nombre + "\n" +
                        "RUT: " + rut,
                Toast.LENGTH_LONG).show();

        // Esperamos 1.5 segundos antes de cerrar (para que se vea el Toast)
        new android.os.Handler().postDelayed(() -> {
            finish(); // Volvemos a PerfilActivity
        }, 1500);
    }
}