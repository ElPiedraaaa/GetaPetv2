package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.CrearToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class MenuUsuarioActivity extends AppCompatActivity {

    private TextView txtPaisCiudad, txtEmail;
    private Button btnMisAnuncios, btnAnimalesAdoptados, btnDarEnAdopcion, btnCambiarDatos, btnAdoptar, btnAdoptarFiltros, btnCerrarSesion;
    private int tipoUser;
    private String idUsuario;
    private WebService webService;
    private String email, pais, ciudad;
    private static final int adios = 3;
    private LayoutInflater layoutInflater;
    private int estado2 = 0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        /*
        Encontramos IDs e iniciamos los ArrayList.
         */

        txtPaisCiudad = findViewById(R.id.txtPaisCiudadMenuUsuarioActivity);
        txtEmail = findViewById(R.id.txtEmailMenuUsuarioActivity);
        btnMisAnuncios = findViewById(R.id.btnMisAnunciosMenuUsuarioActivity);
        btnAnimalesAdoptados = findViewById(R.id.btnAnimalesAdoptadosMenuUsuarioActivity);
        btnDarEnAdopcion = findViewById(R.id.btnDarEnAdopcionMenuUsuarioActivity);
        btnCambiarDatos = findViewById(R.id.btnCambiarDatosMenuUsuarioActivity);
        btnAdoptar = findViewById(R.id.btnAdoptarMenuUsuarioActivity);
        btnAdoptarFiltros = findViewById(R.id.btnAdoptarFiltrosMenuUsuarioActivity);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionMenuUsuarioActivity);

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        layoutInflater = getLayoutInflater();

        setTitle("Menú Usuario");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        tipoUser = getIntent().getIntExtra("tipo_user",0);
        idUsuario = getIntent().getStringExtra("idUsuario");

        /*
        Pedimos permiso al usuario.
         */


        if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
            }
            estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            System.out.println("soy write external storage" + estado2);
        }

        /*
        Select que cogerá toda la información del usuario necesaria para ser mostrada.
         */

        webService = new WebService("select","select email, pais, ciudad from users where id ="+idUsuario);
        webService.execute();

        try {
            String info = webService.get();
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                email = jsonArray.getJSONObject(i).getString("email");
                pais = jsonArray.getJSONObject(i).getString("pais");
                ciudad = jsonArray.getJSONObject(i).getString("ciudad");
            }

            txtEmail.setText(email);
            txtPaisCiudad.setText(ciudad+", "+pais);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }




        btnCambiarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuUsuarioActivity.this, CambiarDatosPersonalesActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnDarEnAdopcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuUsuarioActivity.this,DarEnAdopcionActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnMisAnuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuUsuarioActivity.this, MisAnunciosActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnAdoptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                    {
                        Intent i = new Intent(MenuUsuarioActivity.this, AnunciosAnimalesActivity.class);
                        i.putExtra("tipoUser",tipoUser);
                        i.putExtra("idUsuario",idUsuario);
                        startActivity(i);
                }
            }
        });

        btnAnimalesAdoptados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuUsuarioActivity.this, AnimalesAdoptadosActivity.class );
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnAdoptarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuUsuarioActivity.this, FiltroAnuncioAnimalActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuUsuarioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    int CerrarSesion = 1;
                    Intent i = new Intent(MenuUsuarioActivity.this,IniciarSesionActivity.class);
                    i.putExtra("CerrarSesion",CerrarSesion);
                    startActivity(i);
                }
            }
        });


    }

     /*
    Método para evitar que el usuario pueda volver hacia atrás si le da al boton de atras del movil.
    Es para impedir que el usuario pueda volver a la pantalla de resumen final
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        System.out.println("XDDDDDDDDDDDDD");
        return false;
        // Disable back button..............
    }

    @Override
    protected void onResume() {
        super.onResume();

        webService = new WebService("select","select email, pais, ciudad from users where id ="+idUsuario);
        webService.execute();

        try {
            String info = webService.get();
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                email = jsonArray.getJSONObject(i).getString("email");
                pais = jsonArray.getJSONObject(i).getString("pais");
                ciudad = jsonArray.getJSONObject(i).getString("ciudad");
            }

            txtEmail.setText(email);
            txtPaisCiudad.setText(ciudad+", "+pais);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
