package com.devst.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ConfigActivity extends AppCompatActivity {

    private LinearLayout optionPolitica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Configura la barra de título para volver atrás
        MaterialToolbar toolbar = findViewById(R.id.toolbarConfig);
        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Primero, ejecuta la lógica por defecto (cerrar la activity)
                finish();
                // Luego, aplica la animación
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Encuentra el LinearLayout de la política de privacidad
        optionPolitica = findViewById(R.id.optionPolitica);

        // Configura el listener para abrir el enlace
        optionPolitica.setOnClickListener(v -> {
            String url = getString(R.string.url_terminos_de_uso);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        //lógica para los demás switches y opciones de configuración
    }
}

