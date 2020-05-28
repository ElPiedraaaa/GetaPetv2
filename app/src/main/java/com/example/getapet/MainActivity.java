package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btnCrearUsuario, btnIniciarSesion;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Inicio");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
        Cuando se inicia la aplicación, se borrarán los archivos que la app pudiera tener anteriormente.
         */

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir.isDirectory())
        {
            String[] children = storageDir.list();

            if (children.length == 0)
            {

            }
            else
            {
                for (int i = 0; i < children.length; i++)
                {
                    new File(storageDir, children[i]).delete();
                }
            }
        }

         /*
        Obtenemos todos los IDs.
         */


        btnCrearUsuario = findViewById(R.id.btnCrearUsuarioMainActivity);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesionMainActivity);

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProtectoraOUsuarioActivity.class);
                startActivity(i);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, IniciarSesionActivity.class);
                startActivity(i);
            }
        });

        
    }
}
