package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.CrearToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class IntroducirNuevoOwnerActivity extends AppCompatActivity {

    private TextView txtInfo;
    private EditText txtOwner;
    private Button btnConfirmar;
    private WebService webService;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_nuevo_owner);

           /*
        Obtenemos todos los IDs.
         */

        txtInfo = findViewById(R.id.txtInfoHacerIntroducirNuevoOwnerActivity);
        txtOwner = findViewById(R.id.txtEscribirNuevoOwnerIntroducirNuevoOwnerActivity);
        btnConfirmar = findViewById(R.id.btnConfirmarNuevoOwnerIntroducirNuevoOwnerActivity);

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Introducción Nuevo Propietario");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        final LayoutInflater layoutInflater = getLayoutInflater();

        final int idAnimal = getIntent().getIntExtra("id",0);
        System.out.println("SOY IDANIMAL EN INTRODUCIR NUEVO OWNER "+idAnimal);


        /*
        Esta actividad dará la posibilidad al propietario del animal de adjudicar este al nuevo propietario, dueño de la mascota.
         */

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtOwner.getText().toString().isEmpty())
                {
                    CrearToast crearToast = new CrearToast(IntroducirNuevoOwnerActivity.this,"Escriba un nombre, por favor. ", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    /*
                    Se hace un select para obtener el id del futuro dueño del animal. Si el select no devuelve nada, se le indicará con un mensaje al usuario.
                     */

                    webService = new WebService("select","select id from users where nombre = '"+txtOwner.getText().toString()+"' ");
                    webService.execute();

                    try {
                        String info = webService.get();

                        if (info.equals("0"))
                        {
                            CrearToast crearToast = new CrearToast(IntroducirNuevoOwnerActivity.this,"No existe ese usuario en la base de datos. ", Color.RED, layoutInflater);
                            Toast t = crearToast.CreacionToast();
                            t.show();
                        }
                        else
                        {
                            JSONArray jsonArray = new JSONArray(info);

                            String idUser = "";

                            for (int i = 0; i < jsonArray.length() ; i++)
                            {
                                 idUser = jsonArray.getJSONObject(i).getString("id");
                            }

                            System.out.println("SOY ID USER "+idUser);
                            webService = new WebService("select","select idOwnerActual from animales where id= "+idAnimal);
                            webService.execute();

                            String gat = webService.get();
                            System.out.println("SOY GAT "+gat);
                            jsonArray = new JSONArray(gat);

                            String idOwnerActualPrevio = "";

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                    idOwnerActualPrevio = jsonArray.getJSONObject(i).getString("idOwnerActual");
                            }

                            System.out.println("SOY ID OWNER ACTUAL "+idOwnerActualPrevio);

                            /*
                            Update del valor adoptado al animal y de los ids de los dueños.
                             */

                            webService = new WebService("update","update animales set idOwnerPrevio = "+idOwnerActualPrevio+" , idOwnerActual = "+Integer.parseInt(idUser)+" , adoptado = "+1+"" +
                                    " where id = "+idAnimal+" and adoptado = "+0);
                            webService.execute();

                            String kys = webService.get();

                            if (kys.equals("1"))
                            {
                                CrearToast crearToast = new CrearToast(IntroducirNuevoOwnerActivity.this,"Se ha actualizado el dueño del animal correctamente. ", Color.GREEN, layoutInflater);
                                Toast t = crearToast.CreacionToast();
                                t.show();
                            }
                            else
                            {
                                CrearToast crearToast = new CrearToast(IntroducirNuevoOwnerActivity.this,"Ha ocurrido un error al actualizar el dueño del animal. ", Color.RED, layoutInflater);
                                Toast t = crearToast.CreacionToast();
                                t.show();
                            }

                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
