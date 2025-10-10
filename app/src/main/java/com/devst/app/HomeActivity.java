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

        // 2. Recibir datos del Intent
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);

        // 3. Lógica de la cámara/linterna
        inicializarCamara();


        // 4. Configurar todos los listeners de los botones
        //Evento ir a la camara Intent EXPLÍCITO
        btnCamara.setOnClickListener(v -> { //No tenia la accion de mi boton camara la creo aqui
            if (cameraID != null && luz) {
                try {
                    camara.setTorchMode(cameraID, false);
                } catch (CameraAccessException ignore) {
                } // apaga la linterna si estaba encendida (evita conflictos con la cámara)
                luz = false;
                btnLinterna.setText("Encender Linterna");
            }
            startActivity(new Intent(this, CamaraActivity.class));
        });

        //Evento ir al perfil Intent EXPLÍCITO
        btnIrPerfil.setOnClickListener(view -> { // Configurar botón "Ir a Perfil"
            Intent intentPerfil = new Intent(HomeActivity.this, PerfilActivity.class); // Intent explícito: le decimos exactamente a qué Activity ir - POlimorfismo
            intentPerfil.putExtra("email_usuario", emailUsuario); // Le pasamos el email usando putExtra
            editarPerfilLauncher.launch(intentPerfil);  // Iniciamos la Activity, el profe lo llama solo perfil
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
        }); //Modificado sin la validacion para que sea como la del profe

        //Evento compartir texto con Intent IMPLÍCITO
        btnCompartir.setOnClickListener(view -> { // Configurar botón "Compartir"
            Intent intentCompartir = new Intent(Intent.ACTION_SEND); // Intent implícito para compartir texto
            intentCompartir.setType("text/plain");
            intentCompartir.putExtra(Intent.EXTRA_TEXT, "¡Hola mi nombre es Tiffany, mira esta increíble app! Descárgala desde la Play Store 👌");
            startActivity(Intent.createChooser(intentCompartir, "Compartir mediante:")); // Mostramos el selector de apps para compartir
        });

        //Evento cerrar sesión con Intent IMPLÍCITO
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }
        //Linterna Cámara
        /*Creamos la estructura para el flash principalmente
         * y el inicio de la camara, agregando try-catch*/
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