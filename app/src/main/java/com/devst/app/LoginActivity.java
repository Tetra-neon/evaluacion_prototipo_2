package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.splashscreen.SplashScreen;

public class LoginActivity extends AppCompatActivity {
    // --- 1. VISTAS ---
    // (Se mantienen como variables de instancia porque se usan en onCreate() y en IntentoInicioSesion())
    private EditText edtEmail;
    private EditText edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); //EdgeToEdge es para diseños de pantalla completa en Android, lo puedo quitar
        setContentView(R.layout.activity_login);

        // --- 2. CONECTAR VISTAS Y CONFIGURAR LISTENERS ---
        // Se conectan las vistas del XML a las variables de la clase
        edtEmail = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        Button btnLogin = findViewById(R.id.btnLogin); // Puede ser variable local, ya que solo se usa aquí

        // Se asigna la lógica a cada elemento interactivo
        btnLogin.setOnClickListener(v -> intentoInicioSesion());

        findViewById(R.id.tvRecuperarpass).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: recuperar contraseña", Toast.LENGTH_SHORT).show());
        findViewById(R.id.tvCrear).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: crear cuenta", Toast.LENGTH_SHORT).show());
    }
    //Se validan los campos de entrada y si son correctos, intenta autenticar al usuario
    private void intentoInicioSesion() {
        // --- 3. Obtener y Validar datos ---
        // Se obtiene el texto de los campos, eliminando espacios extra.
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String pass  = edtPass.getText()  != null ? edtPass.getText().toString() : ""; //La contraseña no necesita

        // Validaciones secuenciales.
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Ingresa tu correo");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Correo inválido");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Ingresa tu contraseña");
            edtPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mínimo 6 caracteres");
            edtPass.requestFocus();
            return;
        }

        // --- 4. SIMULACIÓN DE LOGIN Y NAVEGACIÓN ---
        boolean ok = email.equals("t.baron@alumnos.santotomas.cl") && pass.equals("123456");
        if (ok) {
            irAHome(email);
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
    private void irAHome(String emailUsuario) {
        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("email_usuario", emailUsuario);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Esto previene que el usuario pueda presionar "Atrás" desde el Home y volver al Login.
        startActivity(intent);
        finish();
    }
}