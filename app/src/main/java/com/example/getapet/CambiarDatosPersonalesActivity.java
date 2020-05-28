package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
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

public class CambiarDatosPersonalesActivity extends AppCompatActivity {

    private EditText txtContraseña, txtEmail, txtDireccion, txtTelefono;
    private Spinner  spinnerPaís, spinnerCiudad;
    private Button btnCambioContraseña, btnCambioEmail, btnCambioDireccion, btnCambioTelefono, btnCambioPais, btnCambioCiudad;
    private String idUsuario;
    private WebService webService, webService2;
    private ArrayList<String> paises;
    private String idPais;
    private LayoutInflater layoutInflater;
    private ArrayList<String> ciudades;
    private boolean spinnerCiudadTocado, spinnerPaisTocado;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_datos_personales);

        /*
        Obtenemos todos los IDs.
         */

        txtContraseña = findViewById(R.id.txtPasswordCambiarDatosPersonalesActivity);
        txtEmail = findViewById(R.id.txtEmailCambiarDatosPersonalesActivity);
        txtDireccion = findViewById(R.id.txtCambioDireccionCambiarDatosPerfilActivity);
        txtTelefono = findViewById(R.id.txtTelefonoCambioDatosPerfilActivity);
        spinnerPaís = findViewById(R.id.spinnerPaisCambiarDatosPersonalesActivity);
        spinnerCiudad = findViewById(R.id.spinnerCiudadCambiarDatosPersonalesActivity);
        btnCambioContraseña = findViewById(R.id.btnCambioContraseñaCambiarDatosPersonalesActivity);
        btnCambioEmail = findViewById(R.id.btnCambioEmailCambiarDatosPersonalesActivity);
        btnCambioDireccion = findViewById(R.id.btnCambioDireccionCambiarDatosPersonalesActivity);
        btnCambioTelefono = findViewById(R.id.btnCambioTelefonoCambiarDatosPerfilActivity);
        btnCambioPais = findViewById(R.id.btnCambioPaisCambiarDatosPersonalesActivity);
        btnCambioCiudad = findViewById(R.id.btnCambioCiudadCambiarDatosPersona);

          /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Cambiar Datos Personales");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
        Variables boolean que detectarán si se han usado los dos spinners de Ciudad y de País.
         */

        spinnerCiudadTocado = false;
        spinnerPaisTocado = false;

        /*
        Iniciamos ArrayList, obtenemos el LayoutInflater y cogemos el Intent user.
         */

        paises = new ArrayList<>();
        ciudades = new ArrayList<>();
        layoutInflater = getLayoutInflater();

        int user = getIntent().getIntExtra("tipoUser",0);
        idUsuario = getIntent().getStringExtra("idUsuario");

        /*
        Si el user es igual a 1, será que el usuario es un usuario y no una protectora, con lo cual no tiene sentido que cambie la dirección pues no tiene.
         */

        if (user == 1)
        {
            txtDireccion.setVisibility(View.INVISIBLE);
            btnCambioDireccion.setVisibility(View.INVISIBLE);
        }

        /*
        Llamada a la base de datos que obtendrá los países que hay disponibles.
         */
        webService = new WebService("select","select nombre from pais");
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
            spinnerPaís.setAdapter(hintAdapter);
            spinnerPaís.setSelection(hintAdapter.getCount());



        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        Este método se activará cuando se selecciones un dato del spinner de países.
         */
        spinnerPaís.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                 /*
                  Dentro de este método se tendrán que descargar las ciudades de los paises segun el pais
                  escogido. La informacion del spinnerCiudades variará en función a la info del pais
                  cogido.
                   */

                 /*
                 Ponemos spinnerPaisTocado a true.
                  */

                 spinnerPaisTocado = true;

                 /*
                 Obtenemos el nombre del país y nos adjudicamos su ID con esta llamada a la base de datos.
                  */

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

                    /*
                    Obtenemos el nombre de las ciudades descargadas y las añadimos al spinner de ciudades. Ahora el spinner tendrá los datos de las
                    ciudades del país escogido por el usuario.
                     */

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

                    HintAdapter hintAdapter2 = new HintAdapter(CambiarDatosPersonalesActivity.this, R.layout.support_simple_spinner_dropdown_item,ciudades);
                    spinnerCiudad.setAdapter(hintAdapter2);
                    spinnerCiudad.setSelection(hintAdapter2.getCount());

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

        /*
        Método con el que ponemos spinnerCiudad tocado a true
         */

        spinnerCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerCiudadTocado = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        Botones que cambian específicamente algo en concreto. Su funcionamiento es similar en todo, salvo en el caso del país y de la ciudad.
         */

        btnCambioContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtContraseña.getText().toString().isEmpty())
                {
                    /*
                    Toast de error
                     */
                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Introduzca datos, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    // Llamamos al método ConfCambio, que recibirá el dato en cuestión, un tipo de dato para que sepa lo que hacer y el id del Usuario
                    ConfirmacionCambio(txtContraseña.getText().toString(),"password",idUsuario);
                }
            }
        });

        btnCambioEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtEmail.getText().toString().isEmpty())
                {
                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Introduzca datos, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    ConfirmacionCambio(txtEmail.getText().toString(),"email", idUsuario);
                }
            }
        });

        btnCambioTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtTelefono.getText().toString().isEmpty())
                {
                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Introduzca datos, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    ConfirmacionCambio(txtTelefono.getText().toString(),"telefono",idUsuario);
                }
            }
        });

        btnCambioDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtDireccion.getText().toString().isEmpty())
                {
                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Introduzca datos, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    ConfirmacionCambio(txtDireccion.getText().toString(),"direccion",idUsuario);
                }
            }
        });

        /*
        Método que cambiará la ciudad.
         */
        btnCambioCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Primero tenemos que saber si el spinner ha sido tocado y su item cambiado.
                 */
                if (spinnerCiudadTocado)
                {
                    /*
                    Si no ha habido ningún cambio, se detectará y se le pedirá al usuario que escoja una ciudad.
                     */
                    if (spinnerCiudad.getSelectedItem().toString().equals("Escoja una ciudad, por favor"))
                    {
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Debe escoger una ciudad nueva.", Color.RED,layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }
                    else
                    {
                        /*
                             Obtenemos el id Pais de la ciudad que el usuario ha elegido.
                         */

                        System.out.println("ID USUARIO "+idUsuario);
                        String id_pais = null, pais = null, polla = null;
                        webService = new WebService("select","select id_pais from ciudades where nombre = '"+spinnerCiudad.getSelectedItem().toString()+"' ");
                        webService.execute();

                        /*
                        Seleccionamos el nombre del pais del usuario.
                         */

                        webService2 = new WebService("select","select pais from users where id = "+Integer.parseInt(idUsuario));
                        webService2.execute();

                        try {
                            String intel = webService.get();
                            String data = webService2.get();

                            JSONArray json = new JSONArray(intel);
                            JSONArray dobleJSON = new JSONArray(data);

                            for (int i = 0; i < json.length(); i++)
                            {
                                    id_pais = json.getJSONObject(i).getString("id_pais");
                            }

                            for (int i = 0; i < dobleJSON.length() ; i++)
                            {
                                pais = dobleJSON.getJSONObject(i).getString("pais");
                            }


                            /*
                            Obtenemos el id del pais donde se encuentra el usuario.
                             */

                            webService = new WebService("select","select id from pais where nombre = '"+pais+"'");
                            webService.execute();

                            String hola = webService.get();

                            JSONArray json2 = new JSONArray(hola);

                            for (int i = 0; i < json2.length(); i++) {
                                polla = json2.getJSONObject(i).getString("id");
                            }


                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /*
                        Comparamos el id del pais de la ciudad escogida con el id del pais al que pertenece el usuario
                         */

                        if (id_pais.equals(polla))
                        {
                            ConfirmacionCambio(spinnerCiudad.getSelectedItem().toString(),"ciudad",idUsuario);
                        }
                        else
                        {
                            /*
                            Mensaje de error
                             */
                            CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"La ciudad escogida no coincide con el país que nos indicaste anteriormente", Color.RED,layoutInflater);
                            Toast t = crearToast.CreacionToast();
                            t.show();
                        }

                    }
                }
                else
                {
                    /*
                            Mensaje de error
                             */

                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Debe escoger una ciudad nueva.", Color.RED,layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
            }
        });

        /*
        El país cambiará también la ciudad, lógicamente.
         */

        btnCambioPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerPaisTocado && spinnerCiudadTocado)
                {
                    if (spinnerCiudad.getSelectedItem().toString().equals("Escoja una ciudad, por favor"))
                    {
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Debe escoger un país y ciudad nueva.", Color.RED,layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                    }
                    else
                    {
                        ConfirmacionCambio2(spinnerPaís.getSelectedItem().toString(), spinnerCiudad.getSelectedItem().toString(),"pais","ciudad",idUsuario);
                    }
                }
                else
                {
                    CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Debe escoger un país y ciudad nueva.", Color.RED,layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
            }
        });

        /*
        Voy a recibir un dato de la página que me llame , que será si el usuario es un usuario o una protectora.
        Dependiendo de eso, le mostraré una serie de cosas en pantalla.
        Cosas que voy a permitir cambiar al usuario. Contraseña, email, pais, ciudad
        Cosas que voy a permitir cambiar a la protectora: Contraseña, email, pais, ciudad, direccion, telefono.
         */
    }


    /*
    Estos dos AlertDialog crearán un mensaje de confirmación al Usuario para que pueda decidir si realizar el cambio o no.
     */

    public void ConfirmacionCambio(final String dato, final String tipoDato, final String idUsuario)
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(CambiarDatosPersonalesActivity.this);

        builder.setMessage("Cambio de dato del botón apretado. Pulse Confirmar si sigue adelante o Cancelar si no quiere realizar el cambio.").setTitle("Ventana de confirmación.");

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                De esta forma la query se adapta a cualquier petición que se le haga.
                 */
                webService = new WebService("update","update users set "+tipoDato+" = '"+dato+"' where id ="+idUsuario);
                webService.execute();

                try {
                    String info = webService.get();

                    System.out.println("soy infooo "+info);
                    if (info.equals("1"))
                    {
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"La actualización se ha completado correctamente", Color.GREEN,layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }
                    else
                    {
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Ha habido un error. Compruebe su conexión a Internet, por favor.", Color.RED,layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    Alert Dialog que saldrá cuando el usuario quiera cambiar de ciudad. Tiene un funcionamiento similar al anterior.
     */
    public void ConfirmacionCambio2(final String datoPais, final String datoCiudad,   final String tipoDatoPais, final String tipoDatoCiudad, final String idUsuario)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CambiarDatosPersonalesActivity.this);

        builder.setMessage("Cambio de dato del botón apretado. Si está cambiando usted de país, también se actualizará la ciudad que ha puesto usted.").setTitle("Ventana de confirmación.");

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webService = new WebService("update","update users set "+tipoDatoPais+" = '"+datoPais+"' where id ="+Integer.parseInt(idUsuario));
                webService2 = new WebService("update","update users set "+tipoDatoCiudad+" = '"+datoCiudad+"' where id ="+Integer.parseInt(idUsuario));
                webService.execute();
                webService2.execute();

                try {
                    String info = webService.get();
                    String info2 = webService2.get();

                    System.out.println("soy infooo "+info);
                    if (info.equals("1")  && info2.equals("1"))
                    {
                        LayoutInflater layoutInflater = getLayoutInflater();
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"La actualización se ha completado correctamente", Color.GREEN,layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }
                    else
                    {
                        LayoutInflater layoutInflater = getLayoutInflater();
                        CrearToast crearToast = new CrearToast(CambiarDatosPersonalesActivity.this,"Ha habido un error. Compruebe su conexión a Internet, por favor.", Color.RED,layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
