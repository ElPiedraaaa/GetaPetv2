package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.getapet.ComBaseDatos.WebService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class InformacionProtectoraActivity extends AppCompatActivity {

    private int idAnimal;
    private WebService webService;
    private String idOwnerActual;

    private TextView txtNombre, txtEmail, txtPaisCiudad, txtTelefono, txtDireccion;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_protectora);

         /*
        Encontramos IDs e iniciamos los ArrayList.
         */

        txtNombre = findViewById(R.id.txtNombreInformacionProtectoraActivity);
        txtEmail = findViewById(R.id.txtEmailNombreInformacionProtectoraActivity);
        txtPaisCiudad = findViewById(R.id.txtPaisCiudadInformacionProtectoraActivity);
        txtTelefono = findViewById(R.id.txtTelefonoInformacionProtectoraActivity);
        txtDireccion = findViewById(R.id.txtDireccionInformacionProtectoraActivity);

        /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Get A Pet: Información Protectora");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
        Aquí tendremos que recibir los datos de la protectora, es decir, recibir el id del animal, sacar su dueño
        actual y pintar los datos de la protectora.
         */

        idAnimal = getIntent().getIntExtra("idAnimal",0);

        webService = new WebService("select","select idOwnerActual from animales where id="+idAnimal);
        webService.execute();

        try {
            String info = webService.get();
            JSONArray obj = new JSONArray(info);

            for (int i = 0; i < obj.length() ; i++)
            {
                idOwnerActual = obj.getJSONObject(i).getString("idOwnerActual");
            }

            /*
            Obtenemos los datos de la protectora.
             */

            webService = new WebService("select","select id_tipo_usuario, nombre, email, pais, ciudad, telefono, direccion from users where id ="+idOwnerActual);
            webService.execute();

            String dato = webService.get();
            JSONArray json = new JSONArray(dato);

            String id_tipo_Usuario = "", nombre = "", email = "", pais = "", ciudad = "", telefono = "", direccion = "";

            for (int i = 0; i < json.length(); i++)
            {
                 id_tipo_Usuario = json.getJSONObject(i).getString("id_tipo_usuario");
                 nombre = json.getJSONObject(i).getString("nombre");
                 email = json.getJSONObject(i).getString("email");
                 pais = json.getJSONObject(i).getString("pais");
                 ciudad = json.getJSONObject(i).getString("ciudad");
                 telefono = json.getJSONObject(i).getString("telefono");
                 direccion = json.getJSONObject(i).getString("direccion");
            }

            if (id_tipo_Usuario.equals("2"))
            {
                txtDireccion.setVisibility(View.INVISIBLE);
            }

            txtNombre.setText("Nombre: "+nombre);
            txtEmail.setText("Email: "+email);
            txtPaisCiudad.setText("País y ciudad: "+pais+" y "+ciudad);
            txtTelefono.setText("Teléfono: "+telefono);
            txtDireccion.setText("Dirección: "+direccion);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
