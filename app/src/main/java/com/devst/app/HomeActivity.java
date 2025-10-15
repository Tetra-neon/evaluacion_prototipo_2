package com.devst.app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeActivity extends AppCompatActivity {

    // ✅ CORRECCIÓN: Todas las variables que se usan en varios métodos se declaran aquí arriba.
    private TextView tvBienvenida;
    private Button btnLinterna;
    private String emailUsuario;
    private Uri fotoUriParaCamara;

    // Variables para la cámara/linterna
    private CameraManager camara;
    private String cameraID;
    private boolean luz = false;

    // Variables para la ubicación
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // --- Launchers para resultados de otras Activities ---

    private final ActivityResultLauncher<Uri> tomarFotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), exito -> {
                if (exito) {
                    Toast.makeText(this, "Foto guardada en la galería", Toast.LENGTH_SHORT).show();
                } else {
                    if (fotoUriParaCamara != null) {
                        getContentResolver().delete(fotoUriParaCamara, null, null);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if (nombre != null) {
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });

    private final ActivityResultLauncher<String> permisosCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), obtener -> {
                if (obtener) {
                    alternarluz();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // --- 1. Inicialización de Servicios y Launchers ---
        inicializarServiciosDeUbicacion();
        inicializarVistas(); // ✅ CORRECCIÓN: Llamamos a un método para inicializar todas las vistas
        inicializarCamara();
        configurarListeners(); // ✅ CORRECCIÓN: Llamamos a un método para configurar todos los listeners

        // --- 2. Lógica Inicial ---
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Hola: " + emailUsuario);
    }

    private void inicializarVistas() {
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnLinterna = findViewById(R.id.btnLinterna);
        // El resto de los botones se inicializan y usan solo dentro de configurarListeners()
    }

    private void inicializarServiciosDeUbicacion() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ✅ CORRECCIÓN: Se inicializa el launcher de permisos de ubicación UNA SOLA VEZ.
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permiso concedido. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                        obtenerUbicacionYAbrirMapa();
                    } else {
                        Toast.makeText(this, "Permiso denegado. No se puede mostrar la ubicación.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void configurarListeners() {
        // ✅ CORRECCIÓN: Todos los listeners juntos en un solo método.
        Button btnCamara = findViewById(R.id.btnCamara);
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnLlamar = findViewById(R.id.btnLlamar);
        Button btnEnviarSms = findViewById(R.id.btnEnviarSms);
        Button btnAyuda = findViewById(R.id.btnAyuda);
        Button btnConfiguracion = findViewById(R.id.btnConfiguracion);
        Button btnAbrirMapa = findViewById(R.id.btnAbrirMapa);

        btnAbrirMapa.setOnClickListener(v -> verificarPermisosYAbrirMapa());

        btnCamara.setOnClickListener(v -> {
            if (cameraID != null && luz) alternarluz();
            String nombreArchivo = "IMG_" + System.currentTimeMillis() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            fotoUriParaCamara = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (fotoUriParaCamara != null) {
                tomarFotoLauncher.launch(fotoUriParaCamara);
            } else {
                Toast.makeText(this, "No se pudo crear el archivo de imagen.", Toast.LENGTH_SHORT).show();
            }
        });

        btnIrPerfil.setOnClickListener(view -> {
            Intent intentPerfil = new Intent(HomeActivity.this, PerfilActivity.class);
            intentPerfil.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(intentPerfil);
        });

        btnAbrirWeb.setOnClickListener(view -> {
            Uri url = Uri.parse("https://sospetrescue.org/");
            Intent intentWeb = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intentWeb);
        });

        btnEnviarCorreo.setOnClickListener(view -> {
            Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
            intentEmail.setData(Uri.parse("mailto:"));
            intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, "URGENTE: Animal visto");
            intentEmail.putExtra(Intent.EXTRA_TEXT, "Hola quiero reportar un avistamiento de un animal en peligro de extinción en una zona urbana");
            startActivity(Intent.createChooser(intentEmail, "Enviar Correo con:"));
        });

        btnCompartir.setOnClickListener(view -> {
            Intent intentCompartir = new Intent(Intent.ACTION_SEND);
            intentCompartir.setType("text/plain");
            intentCompartir.putExtra(Intent.EXTRA_TEXT, "¡Hola te comparto esta app para reportar avistamientos de animales en peligro de extincion en zonas urbanas! Descárgala desde la Play Store");
            startActivity(Intent.createChooser(intentCompartir, "Compartir mediante:"));
        });

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        btnLlamar.setOnClickListener(view -> {
            String numero = "+56966820967";
            Intent intentLlamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numero));
            if (intentLlamar.resolveActivity(getPackageManager()) != null) {
                startActivity(intentLlamar);
            } else {
                Toast.makeText(this, "No hay app de teléfono disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnEnviarSms.setOnClickListener(view -> {
            String numeroSms = "+56966820967";
            String textoMensaje = "Hola, necesito reportar un avistamiento";
            Intent intentSms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + numeroSms));
            intentSms.putExtra("sms_body", textoMensaje);
            startActivity(intentSms);
        });

        btnAyuda.setOnClickListener(v -> {
            Intent intentAyuda = new Intent(HomeActivity.this, AyudaActivity.class);
            startActivity(intentAyuda);
        });

        btnConfiguracion.setOnClickListener(v -> {
            Intent intentConfig = new Intent(HomeActivity.this, ConfigActivity.class);
            startActivity(intentConfig);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // --- Métodos de la Cámara/Linterna ---
    private void inicializarCamara() {
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleflash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer dato = cc.get(CameraCharacteristics.LENS_FACING);
                if (Boolean.TRUE.equals(disponibleflash) && dato != null && dato == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraID = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "No se puede acceder a la camara", Toast.LENGTH_SHORT).show();
        }
        btnLinterna.setOnClickListener(v -> {
            if (cameraID == null) {
                Toast.makeText(this, "El dispositivo no tiene Flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                alternarluz();
            } else {
                permisosCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void alternarluz() {
        try {
            luz = !luz;
            camara.setTorchMode(cameraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error del controlador de linterna", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Métodos de Ubicación y Mapa ---
    private void verificarPermisosYAbrirMapa() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionYAbrirMapa();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void obtenerUbicacionYAbrirMapa() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            abrirGoogleMaps(location);
                        } else {
                            Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrate de que esté activada.", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de seguridad. Vuelve a intentar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGoogleMaps(Location location) {
        double latitud = location.getLatitude();
        double longitud = location.getLongitude();
        String etiqueta = "Mi ubicación actual";
        Uri gmmIntentUri = Uri.parse("geo:" + latitud + "," + longitud + "?q=" + latitud + "," + longitud + "(" + Uri.encode(etiqueta) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps no está instalado.", Toast.LENGTH_LONG).show();
        }
    }

    // --- Métodos del Ciclo de Vida y Otros ---
    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar", (dialog, which) -> {
                    if (cameraID != null && luz) {
                        try {
                            camara.setTorchMode(cameraID, false);
                            luz = false;
                        } catch (CameraAccessException ignored) {}
                    }
                    Intent intentLogin = new Intent(HomeActivity.this, LoginActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(intentLogin);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraID != null && luz) {
            try {
                camara.setTorchMode(cameraID, false);
                luz = false;
                if (btnLinterna != null) btnLinterna.setText("Encender Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }
}
