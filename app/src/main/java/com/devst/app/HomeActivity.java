package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.provider.MediaStore;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import android.os.Environment;

import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

public class HomeActivity extends AppCompatActivity {

    //variables
    private String emailUsuario; // Variable para guardar el email recibido
    private TextView tvBienvenida; // TextView para mostrar el mensaje de bienvenida
    private Button btnLinterna;
    private CameraManager camara;
    private String cameraID= null;
    private boolean luz = false;
    private Uri fotoUriParaCamara;

    private final ActivityResultLauncher<Uri> tomarFotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    // El "success" confirma que la cámara guardó la foto en la URI que le pasamos.
                    Toast.makeText(this, "Foto guardada en la Galería.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Captura de foto cancelada.", Toast.LENGTH_SHORT).show();
                }
            });

    //Launcher para activity y flash-camara
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if(result.getResultCode() == RESULT_OK && result.getData()!= null){
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if(nombre != null){
                        tvBienvenida.setText("Hola, "+ nombre);
                    }
                }
            });
    private final ActivityResultLauncher<String> permisosCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), obtener ->{
                if(obtener){
                    alternarluz(); //Metodo Apagar y encender Luz
                }else {
                    Toast.makeText(this, "Permiso de camara denegado!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home); //Este es el XML donde estan los botones

        // 1. Inicializar todas las vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnLinterna = findViewById(R.id.btnLinterna); //Botones de la linterna y la camara
        Button btnCamara = findViewById(R.id.btnCamara); // Declarada como variable local
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil); // Conectar los botones del layout
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnLlamar = findViewById(R.id.btnLlamar);
        Button btnEnviarSms = findViewById(R.id.btnEnviarSms);
        Button btnAyuda = findViewById(R.id.btnAyuda);
        Button btnConfiguracion = findViewById(R.id.btnConfiguracion);


        // 2. Recibir datos del Intent
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);

        // 3. Lógica de la cámara/linterna
        inicializarCamara();

        // 4. Configurar todos los listeners de los botones

        //Evento ir a la camara Intent EXPLÍCITO
        btnCamara.setOnClickListener(v -> {
            if (cameraID != null && luz) {
                alternarluz();
            }
            // 1. Crear el nombre y los valores para la nueva imagen en la galería.
            String nombreArchivo = "IMG_" + System.currentTimeMillis() + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            // Le decimos que la guarde en el directorio público de Imágenes.
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            // 2. Crear la URI vacía en la galería pública.
            fotoUriParaCamara = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (fotoUriParaCamara != null) { // 3. Lanzar la cámara, pasándole la URI donde debe guardar la foto.
                tomarFotoLauncher.launch(fotoUriParaCamara);
            } else {
                Toast.makeText(this, "No se pudo crear el archivo de imagen.", Toast.LENGTH_SHORT).show();
            }
        });

        //Evento ir al perfil Intent EXPLÍCITO
        btnIrPerfil.setOnClickListener(view -> { // Configurar botón "Ir a Perfil"
            Intent intentPerfil = new Intent(HomeActivity.this, PerfilActivity.class); // Intent explícito: le decimos exactamente a qué Activity ir - POlimorfismo
            intentPerfil.putExtra("email_usuario", emailUsuario); // Le pasamos el email usando putExtra
            editarPerfilLauncher.launch(intentPerfil);  // Iniciamos la Activity
        });

        //Evento abrir web con Intent IMPLÍCITO
        btnAbrirWeb.setOnClickListener(view -> { // Configurar botón "Abrir Web"
            Uri url = Uri.parse("http://www.santotomas.cl");
            Intent intentWeb = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intentWeb); //el profe lo llama viewWeb
        });
        // Evento Enviar Correo con Intent IMPLÍCITO
        btnEnviarCorreo.setOnClickListener(view -> { // Configurar botón "Enviar Correo"
            Intent intentEmail = new Intent(Intent.ACTION_SENDTO); // Intent implícito para enviar email el profe lo llama solo email
            intentEmail.setData(Uri.parse("mailto:")); // Solo apps de email por ahora jeje
            intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Asunto de Android");
            intentEmail.putExtra(Intent.EXTRA_TEXT, "Mensaje del cuerpo(contenido)");
            startActivity(Intent.createChooser(intentEmail, "Enviar Correo con:"));
        });

        //Evento compartir texto con Intent IMPLÍCITO
        btnCompartir.setOnClickListener(view -> { // Configurar botón "Compartir"
            Intent intentCompartir = new Intent(Intent.ACTION_SEND); // Intent implícito para compartir texto
            intentCompartir.setType("text/plain");
            intentCompartir.putExtra(Intent.EXTRA_TEXT, "¡Hola mi nombre es Tiffany, mira esta increíble app! Descárgala desde la Play Store 👌");
            startActivity(Intent.createChooser(intentCompartir, "Compartir mediante:")); // Mostramos el selector de apps para compartir
        });

        //Evento cerrar sesión con Intent IMPLÍCITO
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        //Evento llamar con Intent IMPLÍCITO
        btnLlamar.setOnClickListener(view -> { // Configurar botón "Llamar"
            Intent intentLlamar = new Intent(Intent.ACTION_DIAL); // Intent implícito para llamar
            startActivity(intentLlamar); // Iniciamos la Activity
        });

        // Evento Enviar SMS con Intent IMPLÍCITO
        btnEnviarSms.setOnClickListener(view -> { // Configurar botón "Enviar SMS"
            String numeroSms = "";
            String textoMensaje = "Hola, necesito ayuda con la aplicación.";
            Intent intentSms = new Intent(Intent.ACTION_SENDTO,  Uri.parse("smsto:" + numeroSms)); // Intent implícito para enviar SMS
            intentSms.putExtra("sms_body", textoMensaje);
            startActivity(intentSms);
        });

        // Evento ir a la Ayuda con Intent EXPLÍCITO
        btnAyuda.setOnClickListener(v -> {
            // Creamos un Intent explícito porque sabemos exactamente a qué Activity queremos ir.
            Intent intentAyuda = new Intent(HomeActivity.this, AyudaActivity.class);
            startActivity(intentAyuda); // Iniciamos la AyudaActivity
        });

        //Evento ir a la configuración con Intent EXPLÍCITO
        btnConfiguracion.setOnClickListener(v -> {
            // Creamos un Intent explícito porque sabemos exactamente a qué Activity queremos ir.
            Intent intentConfig = new Intent(HomeActivity.this, ConfigActivity.class);
            startActivity(intentConfig); // Iniciamos la AyudaActivity
        });

    }
        //Logica de linterna
    private void inicializarCamara() {
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for(String id: camara.getCameraIdList()){
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleflash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer dato = cc.get(CameraCharacteristics.LENS_FACING);
                if(Boolean.TRUE.equals(disponibleflash)
                        && dato != null
                        && dato ==CameraCharacteristics.LENS_FACING_BACK){
                    cameraID = id; //Priorizar la camara trasera con el flash
                    break;
                }
            }
        }catch (CameraAccessException e){
            Toast.makeText(this, "No se puede acceder a la camara", Toast.LENGTH_SHORT).show();
        }
        btnLinterna.setOnClickListener(v -> {
            if(cameraID == null){
                Toast.makeText(this, "El dispositivo no tiene Flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            //Datos de carga de permisos
            boolean cargadato = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            if(cargadato){
                alternarluz();
            }else{ // aqui uso mi permisosCamaraLauncher
                permisosCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

    }
    private void alternarluz() {
        try {
            luz =! luz;
            camara.setTorchMode(cameraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        }catch (CameraAccessException e){
            Toast.makeText(this, "Error del controlador de linterna", Toast.LENGTH_SHORT).show();
        }
    }
    private void cerrarSesion() {
        new AlertDialog.Builder(this) // Mostrar diálogo de confirmación
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar", (dialog, which) -> {
                    if (cameraID != null && luz) { // Apagar la linterna si está encendida
                        try {
                            camara.setTorchMode(cameraID, false);
                            luz = false;
                        } catch (CameraAccessException ignore) {
                        }
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
    protected void onPause(){
        super.onPause();
        if(cameraID != null && luz){ //// Si la linterna está encendida
            try {
                camara.setTorchMode(cameraID, false); // Apaga la linterna
                luz = false;
                //Actualizamos el texto del botón
                if(btnLinterna != null) btnLinterna.setText("Encender Linterna"); //Esta comprobación no es estrictamente necesaria
            }catch (CameraAccessException ignore){}                               //btnLinterna se inicializa en onCreate,
        }                                                                         //y onPause siempre se ejecutará después de que onCreate haya terminado
    }                                                                             //Por lo tanto, en el ciclo de vida normal de una Activity,
}                                                                                 //btnLinterna nunca será nulo cuando se llame a onPause.
                                                                                  //Es decir, que se puede quitar la comprobacion de nulidad asi el codigo es mas directo jeje