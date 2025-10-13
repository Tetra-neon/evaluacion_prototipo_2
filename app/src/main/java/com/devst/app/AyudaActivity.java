package com.devst.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AyudaActivity extends AppCompatActivity {

    // Declaramos los componentes del UI
    private MaterialToolbar toolbar;
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputEditText etMensaje;
    private TextInputLayout tilMensaje, menuTipoContacto;
    private Button btnEnviarFormulario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        // Inicializar los componentes
        toolbar = findViewById(R.id.toolbarAyuda);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        etMensaje = findViewById(R.id.etMensaje);
        tilMensaje = findViewById(R.id.tilMensaje);
        menuTipoContacto = findViewById(R.id.menuTipoContacto);
        btnEnviarFormulario = findViewById(R.id.btnEnviarFormulario);

        // Configurar la barra de título para volver atrás
        configurarToolbar();

        // Configurar el menú desplegable
        configurarMenuDesplegable();

        // Configurar el listener del botón de enviar
        btnEnviarFormulario.setOnClickListener(v -> validarYEnviarFormulario());
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarMenuDesplegable() {
        // Obtenemos el array de strings desde el archivo arrays.xml
        String[] tipos = getResources().getStringArray(R.array.tipos_contacto);

        // Creamos un adaptador para el AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipos);

        // Asignamos el adaptador
        autoCompleteTextView.setAdapter(adapter);
    }

    private void validarYEnviarFormulario() {
        // Obtenemos los valores de los campos
        String tipoContacto = autoCompleteTextView.getText().toString();
        String mensaje = etMensaje.getText().toString().trim();

        // Limpiamos errores previos
        menuTipoContacto.setError(null);
        tilMensaje.setError(null);

        // ----- VALIDACIONES -----
        boolean esValido = true;

        if (TextUtils.isEmpty(tipoContacto)) {
            menuTipoContacto.setError("Debes seleccionar un motivo");
            esValido = false;
        }

        if (TextUtils.isEmpty(mensaje)) {
            tilMensaje.setError("El mensaje no puede estar vacío");
            esValido = false;
        } else if (mensaje.length() < 10) {
            tilMensaje.setError("El mensaje debe tener al menos 10 caracteres");
            esValido = false;
        }

        // Si alguna validación falló, no continuamos
        if (!esValido) {
            return;
        }

        // ----- ENVÍO EXITOSO -----

        // Formateamos el mensaje de éxito
        String tipoFormateado = tipoContacto;
        if (tipoContacto.equals("Solicitud de Ayuda")) {
            tipoFormateado = "solicitud";
        } else {
            // Convierte "Reclamo" a "reclamo", etc.
            tipoFormateado = tipoFormateado.toLowerCase();
        }

        String mensajeExito = "Se ha enviado con éxito su " + tipoFormateado + ".";

        // Mostramos el Toast de confirmación
        Toast.makeText(this, mensajeExito, Toast.LENGTH_LONG).show();

        // Opcional: Limpiar los campos y cerrar la pantalla después de enviar
        autoCompleteTextView.setText("", false); // el 'false' evita que se vuelva a mostrar el dropdown
        etMensaje.setText("");

        // Podrías llamar a finish() si quieres que el usuario regrese a la pantalla anterior
        // finish();
    }
}
