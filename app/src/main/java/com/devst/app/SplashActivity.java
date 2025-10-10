package com.devst.app;

import android.content.Intent;
import android.os.Bundle;import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Instala el SplashScreen. ¡DEBE ir antes de super.onCreate()!
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // 2. Define la animación personalizada para el ícono
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            // Carga la animación que creamos
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation);

            // Aplica la animación al ícono del splash screen
            splashScreenView.getIconView().startAnimation(animation);

            // Escucha cuando la animación termine para quitar la vista del splash
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Cuando la animación del logo termine, quita la splash screen
                    splashScreenView.remove();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        // 3. Redirige a la LoginActivity
        // El sistema esperará a que la app esté lista y luego hará la transición.
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Cierra SplashActivity para que no se pueda volver a ella
    }
}
