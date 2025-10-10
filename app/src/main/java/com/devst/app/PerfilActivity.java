package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {
    // Variables para los elementos del layout
    private TextView tvEmailPerfil;
    private Button btnEditarPerfil;
    private Button btnVolverHome;
    private String emailUsuario; // Variable para guardar el email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // 1. CONECTAR las vistas del XML con las variables Java
        tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnVolverHome = findViewById(R.id.btnVolverHome);

        // 2. RECIBIR el email que viene del HomeActivity
        // Usamos getIntent() para obtener el Intent que inició esta Activity
        Intent intent = getIntent();

        // Verificamos si el Intent trae datos extras
        if (intent != null && intent.hasExtra("email_usuario")) {
            // Extraemos el email usando la misma clave "email_usuario"
            emailUsuario = intent.getStringExtra("email_usuario");

            // Mostramos el email en el TextView
            tvEmailPerfil.setText(emailUsuario);
        } else {
            // Si no hay email, mostramos un mensaje por defecto
            tvEmailPerfil.setText("No se pudo cargar el email");
            Toast.makeText(this, "Error: no se recibió el email", Toast.LENGTH_SHORT).show();
        }

        // 3. CONFIGURAR el botón "Editar Perfil"
        btnEditarPerfil.setOnClickListener(v -> {
            // Creamos un Intent para ir a EditarPerfilActivity
            Intent intentEditar = new Intent(PerfilActivity.this, EditarPerfilActivity.class);

            // Le pasamos el email para que también lo tenga disponible
            intentEditar.putExtra("email_usuario", emailUsuario);

            // Iniciamos la nueva Activity
            startActivity(intentEditar);
        });

        // 4. CONFIGURAR el botón "Volver al Home"
        btnVolverHome.setOnClickListener(v -> {
            // Simplemente cerramos esta Activity para volver a la anterior
            finish();
        });
    }
}