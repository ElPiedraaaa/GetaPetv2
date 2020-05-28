package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

public class MenuProtectoraActivity extends AppCompatActivity {

    private Button btnmisAnuncios, btnDarenAdopcion, btnCambiarDatos, btnCerrarSesion;
    private TextView txtDireccion, txtEmail, txtTelefono, txtPaisCiudad;
    private WebService webService;
    private String email, direccion, telefono, pais, ciudad, idUsuario;
    private int tipoUser;
    private static final int adios = 3;
    private LayoutInflater layoutInflater;
    private int estado2 = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_protectora);

        /*
        Encontramos IDs e iniciamos los ArrayList.
         */

        btnmisAnuncios = findViewById(R.id.btnMisAnunciosAnimalesMenuProtectoraActivity);
        btnDarenAdopcion = findViewById(R.id.btnDarEnAdopcionMenuProtectoraActivity);
        btnCambiarDatos = findViewById(R.id.btnCambiarDatosMenuProtectoraActivity);
        txtDireccion = findViewById(R.id.txtDireccionMenuProtectoraActivity);
        txtEmail = findViewById(R.id.txtEmailMenuProtectoraActivity);
        txtTelefono = findViewById(R.id.txtTelefonoMenuProtectoraActivity);
        txtPaisCiudad = findViewById(R.id.txtPaisCiudadMenuProtectoraActivity);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionMenuProtectoraActivity);
        layoutInflater = getLayoutInflater();

          /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Menú Protectora");
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

        if (ActivityCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
            }
            estado2 = ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            System.out.println("soy write external storage" + estado2);
        }

        /*
        Select que selecciona toda la información del usuario, que se mostrará en el perfil para que el usuario vea sus propios datos.
         */

        webService = new WebService("select","select email, telefono, direccion, pais, ciudad from users where id ="+Integer.parseInt(idUsuario));
        webService.execute();

        try {
            String info = webService.get();
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                email = jsonArray.getJSONObject(i).getString("email");
                direccion = jsonArray.getJSONObject(i).getString("direccion");
                telefono = jsonArray.getJSONObject(i).getString("telefono");
                pais = jsonArray.getJSONObject(i).getString("pais");
                ciudad = jsonArray.getJSONObject(i).getString("ciudad");
            }

            txtEmail.setText(email);
            txtDireccion.setText("Dirección: "+direccion);
            txtTelefono.setText("Teléfono: "+telefono);
            txtPaisCiudad.setText("Ciudad y país: "+ciudad+", "+pais);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        El funcionamiento de los botones es similar en los 4 botones. Si el usuario no le ha dado permisos a la aplicación, cada vez que pulse un botón se le volverán a pedir permisos.
         */

        btnCambiarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("BB "+ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE));

                if (ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuProtectoraActivity.this, CambiarDatosPersonalesActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnDarenAdopcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("SOY ESTADO 2 "+estado2);
                if (ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuProtectoraActivity.this,DarEnAdopcionActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnmisAnuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuProtectoraActivity.this, MisAnunciosActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    startActivity(i);
                }
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                {
                    if (ActivityCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, adios);
                        }
                        estado2 = ContextCompat.checkSelfPermission(MenuProtectoraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        System.out.println("soy write external storage" + estado2);
                    }
                }
                else
                {
                    Intent i = new Intent(MenuProtectoraActivity.this, IniciarSesionActivity.class);
                    i.putExtra("tipoUser",tipoUser);
                    i.putExtra("idUsuario",idUsuario);
                    i.putExtra("CerrarSesion",1);
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

    /*
    On Resume, porque algunas veces la aplicacion actualizará los datos del usuario y se tendrán que mostrar actualizados.
     */

    @Override
    protected void onResume() {
        super.onResume();

        webService = new WebService("select","select email, telefono, direccion, pais, ciudad from users where id ="+idUsuario);
        webService.execute();

        try {
            String info = webService.get();
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                email = jsonArray.getJSONObject(i).getString("email");
                direccion = jsonArray.getJSONObject(i).getString("direccion");
                telefono = jsonArray.getJSONObject(i).getString("telefono");
                pais = jsonArray.getJSONObject(i).getString("pais");
                ciudad = jsonArray.getJSONObject(i).getString("ciudad");
            }

            txtEmail.setText(email);
            txtDireccion.setText("Dirección: "+direccion);
            txtTelefono.setText("Teléfono: "+telefono);
            txtPaisCiudad.setText("Ciudad y país: "+ciudad+", "+pais);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
