package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.CrearToast;
import com.example.getapet.pojo.HintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class IntroduccionDatosUsuarioActivity extends AppCompatActivity {

    private EditText txtNombre, txtEmail, txtContraseña, txtConfContraseña, txtTelefono;
    private Spinner spinnerPais, spinnerLocalidad;
    private Button btnConfirmar;
    private WebService webService;
    private ArrayList<String> paises;
    private String idPais;
    private LayoutInflater layoutInflater;
    private String idUsuario;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduccion_datos_usuario);

        /*
        Encontramos IDs, iniciamos ArrayLists, y el Layout Inflater.
         */

        txtNombre = findViewById(R.id.txtNombreIntroduccionDatosUsuarioActivity);
        txtEmail = findViewById(R.id.txtEmailIntroduccionDatosUsuarioActivity);
        txtTelefono = findViewById(R.id.txtTelefonoIntroduccionDatosUsuario);
        spinnerPais = findViewById(R.id.spinnerPaisIntroduccionDatosUsuarioActivity);
        spinnerLocalidad = findViewById(R.id.spinnerLocalidadIntroduccionDatosUsuarioActivity);
        btnConfirmar = findViewById(R.id.btnConfirmarDatosIntroduccionDatosUsuarioActivity);
        txtContraseña = findViewById(R.id.txtContraseñaIntroduccionDatosUsuarioActivity);
        txtConfContraseña = findViewById(R.id.txtConfContraseñaIntroduccionDatosUsuarioActivity);

        layoutInflater = getLayoutInflater();

        paises= new ArrayList<>();

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Introducción Datos Usuario");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
         Vamos a configurar los spinners para que muestren los datos que deben mostrar. El Spinner Pais
         deberá de descargar siempre los datos de los paises que hay al principio, y en función del país
         seleccionado, se descargarán unas ciudades u otras.


         El funcionamiento de esta actividad es similar al de IntroduccionDatosProtectoraActivity

          */

         /*
         Llamada a la base de datos. Recogemos todos los paises que tenemos.
          */
        webService = new WebService("select","select id, nombre from pais");
        webService.execute();

        try {
            /*
            Recogemos los datos en un String y los metemos en un JSONArray para poder desmenuzarlos
             */
            String data = webService.get();
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length() ; i++)
            {
                String pais = jsonArray.getJSONObject(i).getString("nombre");
                paises.add(pais);
            }

            /*
            Añadimos el hint en el ultimo lugar, ya que HintAdapter hará que el último dato se muestre como una indicación.
             */
            paises.add("Escoja un país, por favor");

            /*
            Seteamos la información
             */
            HintAdapter hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,paises);
            spinnerPais.setAdapter(hintAdapter);
            spinnerPais.setSelection(hintAdapter.getCount());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                  /*
                  Dentro de este método se tendrán que descargar las ciudades de los paises segun el pais
                  escogido. La informacion del spinnerCiudades variará en función a la info del pais
                  cogido.
                   */
                ArrayList<String> ciudades = new ArrayList<>();

                String item = parent.getItemAtPosition(position).toString();
                System.out.println("ITEM "+item);
                webService = new WebService("select","select id from pais where nombre = '"+item+"'");
                webService.execute();

                try {
                    String info = webService.get();
                    System.out.println("INFO "+info);
                    JSONArray jsonArray = new JSONArray(info);

                    idPais = jsonArray.getJSONObject(0).getString("id");
                    System.out.println("id Pais "+idPais);

                    webService = new WebService("select","select nombre from ciudades where id_pais = "+idPais);
                    webService.execute();

                    String info2 = webService.get();
                    JSONArray jsonArray1 = new JSONArray(info2);

                    for (int i = 0; i < jsonArray1.length(); i++)
                    {
                        String ciutats = jsonArray1.getJSONObject(i).getString("nombre");
                        ciudades.add(ciutats);
                    }

                    ciudades.add("Escoja una ciudad, por favor");

                    HintAdapter hintAdapter2 = new HintAdapter(IntroduccionDatosUsuarioActivity.this, R.layout.support_simple_spinner_dropdown_item,ciudades);
                    spinnerLocalidad.setAdapter(hintAdapter2);
                    spinnerLocalidad.setSelection(hintAdapter2.getCount());

                    System.out.println("Hemos llegado hasta aqui");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLocalidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         /*
         Comprobamos si todos los campos de la vista están rellenos con información
          */
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (txtNombre.getText().toString().isEmpty() || txtEmail.getText().toString().isEmpty() || txtTelefono.getText().toString().isEmpty()
                        || txtContraseña.getText().toString().isEmpty() || txtConfContraseña.getText().toString()
                        .isEmpty() || spinnerPais.getSelectedItem() == null || spinnerLocalidad.getSelectedItem() == null
                        || spinnerLocalidad.getSelectedItem().equals("Escoja una ciudad, por favor") || spinnerPais.getSelectedItem().equals("Escoja un país, por favor"))
                {
                    CrearToast crearToast = new CrearToast(IntroduccionDatosUsuarioActivity.this,"Introduce todos los datos necesarios, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {

                    webService = new WebService("select","select nombre from users where nombre = '"+txtNombre.getText().toString()+"'");
                    webService.execute();

                    try {
                        String data = webService.get();
                        System.out.println("SOY DATA "+data);
                        if (data.equals("0"))
                        {
                            if (!txtContraseña.getText().toString().equals(txtConfContraseña.getText().toString()))
                            {
                                CrearToast crearToast = new CrearToast(IntroduccionDatosUsuarioActivity.this,"Las contraseñas no coinciden.", Color.RED, layoutInflater);
                                Toast t = crearToast.CreacionToast();
                                t.show();
                            }
                            else
                            {
                                webService = new WebService("insert","insert into users(id_tipo_usuario, nombre, email, password, pais, ciudad, telefono , " +
                                        " fecha) values ("+1+" , '"+txtNombre.getText().toString()+"' , '"+txtEmail.getText().toString()+"' , '"+txtContraseña.getText().toString()+
                                        "' , '"+spinnerPais.getSelectedItem().toString()+"' , '"+spinnerLocalidad.getSelectedItem().toString()+"' , "+Integer.parseInt(txtTelefono.getText().toString())+" , date(now()))");
                                webService.execute();

                                String ok = webService.get();
                                System.out.println("soy ok "+ok);
                                if (ok.equals("1"))
                                {
                                    webService = new WebService("select","select id from users where nombre = '"+txtNombre.getText().toString()+"'");
                                    webService.execute();

                                    String datin = webService.get();

                                    JSONArray jsonArray = new JSONArray(datin);

                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        idUsuario = jsonArray.getJSONObject(i).getString("id");
                                    }

                                    System.out.println("SOY ID USUARIO "+idUsuario);

                                    Intent i = new Intent(IntroduccionDatosUsuarioActivity.this,MenuUsuarioActivity.class);
                                    i.putExtra("tipo_user",1);
                                    i.putExtra("idUsuario",idUsuario);
                                    startActivity(i);
                                }
                                else
                                {
                                    CrearToast crearToast = new CrearToast(IntroduccionDatosUsuarioActivity.this,"Ha ocurrido un error, disculpe las molestias.", Color.RED, layoutInflater);
                                    Toast t = crearToast.CreacionToast();
                                    t.show();
                                }
                            }
                        }
                        else
                        {
                            CrearToast crearToast = new CrearToast(IntroduccionDatosUsuarioActivity.this,"El nombre introducido ya pertenece a un usuario.", Color.RED, layoutInflater);
                            Toast t = crearToast.CreacionToast();
                            t.show();
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
