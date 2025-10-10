package com.devst.app;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CamaraActivity extends AppCompatActivity {

    private static final String STATE_URI = "photo_uri";
    private static final String STATE_LAUNCHED = "has_launched";

    @Nullable private Uri photoUri;
    private boolean hasLaunched = false; // evita relanzar en rotaciones
    private ImageView preview;           // muestra la foto tomada

    // Pide permiso de cámara si falta
    private final ActivityResultLauncher<String> permisoCamara =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCapture();
                else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    // Abre la cámara y guarda directamente en el Uri (Galería)
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> {
                if (ok && photoUri != null) {

                    try {
                        if (preview != null) {
                            if (Build.VERSION.SDK_INT >= 28) {
                                var src = ImageDecoder.createSource(getContentResolver(), photoUri);
                                var bmp = ImageDecoder.decodeBitmap(src);
                                preview.setImageBitmap(bmp);
                            } else {
                                preview.setImageURI(photoUri);
                            }
                        }
                        Toast.makeText(this, "Foto guardada en Galería", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Guardada, pero no se pudo previsualizar", Toast.LENGTH_SHORT).show();
                    }

                    // --- Bloque para relanzar la captura automáticamente ---
                    // Se usa un Handler para introducir un pequeño retraso
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(() -> {
                                // Resetea el estado JUSTO ANTES de la nueva captura.
                                // Esto evita problemas si el usuario rota la pantalla durante el retraso.
                                photoUri = null;
                                hasLaunched = false;
                                startCapture(); // Inicia la siguiente captura
                            }, 350); // 350 milisegundos de retraso

                } else {
                    Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show();
                    if (photoUri != null) {
                        getContentResolver().delete(photoUri, null, null); // Borra el registro vacío en la galería
                        photoUri = null;
                    }
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camara);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            var bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        preview = findViewById(R.id.imgPreview);

        // Restaurar estado (evita relanzar la cámara en rotación)
        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(STATE_URI, null);
            if (saved != null) photoUri = Uri.parse(saved);
            hasLaunched = savedInstanceState.getBoolean(STATE_LAUNCHED, false);
        }

        // Lanzar automáticamente la cámara (una sola vez)
        if (!hasLaunched) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                startCapture();
            } else {
                permisoCamara.launch(Manifest.permission.CAMERA);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoUri != null) outState.putString(STATE_URI, photoUri.toString());
        outState.putBoolean(STATE_LAUNCHED, hasLaunched);
    }
    private void startCapture() {
        if (photoUri == null) photoUri = createPhotoUri();
        if (photoUri == null) {
            Toast.makeText(this, "No se pudo crear destino de imagen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        hasLaunched = true;
        takePicture.launch(photoUri);
    }
    @Nullable
    private Uri createPhotoUri() {
        String nombre = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) + ".jpg";
        var cv = new ContentValues();
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, nombre);
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MiAppPruebas");
        }
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
    }
}
