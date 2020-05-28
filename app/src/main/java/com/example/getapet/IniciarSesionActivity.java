package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.CrearToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class IniciarSesionActivity extends AppCompatActivity {

    private TextView txtContraseñaOlvidada;
    private EditText txtNombre, txtContraseña;
    private Switch switch1;
    private Button btnIniciarSesion1;
    private WebService webService;
    private String nombreBD, passBD, tipoUsuarioBD, idUsuarioBD;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_iniciar_sesion);

        /*
        Encontramos IDs e iniciamos los ArrayList.
         */

        txtContraseñaOlvidada = findViewById(R.id.txtOlvidarContraseñaIniciarSesionActivity);
        txtNombre = findViewById(R.id.txtNombreIniciarSesionActivity);
        txtContraseña = findViewById(R.id.txtContraseñaIniciarSesionActivity);
        switch1 = findViewById(R.id.switchIniciarSesionActivity);
        btnIniciarSesion1 = findViewById(R.id.btnIniciarSesionIniciarSesionActivity);

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Iniciar Sesión");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        int CerrarSesion = 0;

        /*
        Intent que sólo tendrá valor cuando se vuelva del cerrar sesión del menú, con el cual se borrarán las shared preferences.
         */

        CerrarSesion = getIntent().getIntExtra("CerrarSesion",0);

        System.out.println("Cerrar SESION "+CerrarSesion);

        if (CerrarSesion == 1)
        {
            this.getSharedPreferences("password", Context.MODE_PRIVATE).edit().clear().apply();
        }


        /*
        El propósito de esta actividad es que el usuario introduzca sus datos para hacer el login en la base
        de datos. Si este login es correcto, el usuario pasará a la siguiente actividad. Sino, no pasará.
        El usuario también podrá activar el login persistente si lo desea o resetear su contraseña, con lo cual se le mandará
        un e-mail para ese propósito.
         */

        /*
        Obtenemos las shared preferences, si bool es true, hacemos un intent directamente y que la aplicación vaya directamente al menú correspondiente.
         */

        SharedPreferences preferences = getSharedPreferences("password",Context.MODE_PRIVATE);
        Boolean bool = preferences.getBoolean("bool",false);
        int tipo = preferences.getInt("tipoUsuario",0);
        String idUsuario = preferences.getString("idUsuario"," ");

        System.out.println("BOOL "+bool);
        System.out.println("SOY TIPO "+tipo);
        System.out.println("SOY IDUSUARIO "+idUsuario);

        if (bool)
        {
            if (tipo == 2)
            {
                Intent i = new Intent(IniciarSesionActivity.this,MenuProtectoraActivity.class);
                i.putExtra("tipo_user",tipo);
                i.putExtra("idUsuario",idUsuario);
                startActivity(i);
            }
            else
            {
                Intent i = new Intent(IniciarSesionActivity.this,MenuUsuarioActivity.class);
                i.putExtra("tipo_user",tipo);
                i.putExtra("idUsuario",idUsuario);
                startActivity(i);
            }
        }

        /*
        Método que se ejecutará cuando el usuario le de al botón de iniciar Sesión
         */

        btnIniciarSesion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*
                Pedimos la info al web service.
                 */
                webService = new WebService("select","select id, id_tipo_usuario, nombre, password from users where nombre = '"+txtNombre.getText().toString()+"'");
                webService.execute();

                try {
                    String info = webService.get();

                    JSONArray obj = new JSONArray(info);

                    for (int i = 0; i < obj.length(); i++)
                    {
                        tipoUsuarioBD = obj.getJSONObject(i).getString("id_tipo_usuario");
                        nombreBD = obj.getJSONObject(i).getString("nombre");
                        passBD = obj.getJSONObject(i).getString("password");
                        idUsuarioBD = obj.getJSONObject(i).getString("id");
                    }

                    System.out.println("SOY TIPO USUARIO BD "+tipoUsuarioBD);
                    System.out.println("SOY CONTRASEÑA BD "+passBD);


                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*
                Si la info de la base de datos y la que ha introducido el usuario coinciden, se hace el login y se guardarían las
                shared preferences. Sino, no se hace el login y se borran las shared preferences. Habrá que ver cómo se hace esto.
                 */
                if (txtNombre.getText().toString().equals(nombreBD) && txtContraseña.getText().toString().equals(passBD))
                {
                    if (tipoUsuarioBD.equals("2"))
                    {

                        /*
                        Si el switch es checkeado, se guarda la info introducida por el usuario en el shared preferences.
                         */
                        if (switch1.isChecked())
                        {
                            guardarPreferencias(tipoUsuarioBD, idUsuarioBD);
                        }

                        Intent i = new Intent(IniciarSesionActivity.this, MenuProtectoraActivity.class);
                        i.putExtra("tipo_user",Integer.parseInt(tipoUsuarioBD));
                        i.putExtra("idUsuario",idUsuarioBD);
                        startActivity(i);
                    }
                    else
                    {
                        if (switch1.isChecked())
                        {
                            guardarPreferencias(tipoUsuarioBD, idUsuarioBD);
                        }

                        Intent i = new Intent(IniciarSesionActivity.this, MenuUsuarioActivity.class);
                        i.putExtra("tipo_user",Integer.parseInt(tipoUsuarioBD));
                        i.putExtra("idUsuario",idUsuarioBD);
                        startActivity(i);
                    }
                }
                else
                {
                    /*
                    Mensaje de error.
                     */

                    LayoutInflater layoutInflater = getLayoutInflater();
                    CrearToast crearToast = new CrearToast(IniciarSesionActivity.this,"Inicio de sesión fallido.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
            }
        });

        /*
        Código que se ejecutará cuando el usuario haya olvidado la contraseña y desee recuperarla. Este código
        cogerá el email de la base de datos y enviara un correo con una contraseña que es la que deberá utilizar
        el usuario para poder acceder a su cuenta.
         */
        txtContraseñaOlvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(IniciarSesionActivity.this);

                builder.setMessage("Se va a enviar un email a la dirección de correo electrónico del usuario. \n Recibirás" +
                        " un código estándar que será la nueva contraseña con la que debes iniciar sesión. \n Pulsa confirmar " +
                        "si quieres realizar esta acción o cancelar si te lo has pensado mejor")
                        .setTitle("Confirmación");

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
                        Aquí es donde se enviaría al usuario a una nueva actividad, para que pueda introducir el nombre.
                         */
                        Intent i = new Intent(IniciarSesionActivity.this,RecuperarPasswordActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    /*
    Para guardar la info en el shared preference.
     */
    private void guardarPreferencias(String tipoUsuario, String idUsuario)
    {
        SharedPreferences preferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        boolean shared = true;

        int tipoUser = Integer.parseInt(tipoUsuario);

        SharedPreferences.Editor editor= preferences.edit();

        editor.putBoolean("bool",shared);
        editor.putInt("tipoUsuario",tipoUser);
        editor.putString("idUsuario", idUsuario);
        System.out.println("SOY TIPO DE DATO idUsuario "+idUsuario.getClass());
        editor.apply();
    }

}
