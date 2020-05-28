package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class RecuperarPasswordActivity extends AppCompatActivity {

    private EditText txtNombre, txtEmail;
    private TextView txtMostrarContraseña;
    private Button btnConfirmar;
    private WebService webService;
    private String nombre,email;
    private LayoutInflater layoutInflater;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

           /*
        Encontramos IDs e iniciamos los ArrayList.
         */

        txtNombre = findViewById(R.id.txtNombreCuentaRecuperarPasswordActivity);
        txtEmail = findViewById(R.id.txtEmailCuentaRecuperarPasswordActivity);
        btnConfirmar = findViewById(R.id.btnConfirmarRecuperarPasswordActivity);
        txtMostrarContraseña = findViewById(R.id.txtContraseñaNuevaRecuperarPasswordActivity);

        layoutInflater = getLayoutInflater();

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Get A Pet: Recuperar Password");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
        Actividad que pedirá al usuario un nombre y un email para poder cambiar la contraseña. Si el email
        y el nombre coinciden con los que están en la base de datos, el usuario recibirá un email con un código que
        será su nueva contraseña. Dicho esto, podrá entrar en su perfil con esa contraseña y ya ahí cambiarla.
         */

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNombre.getText().toString().isEmpty() || txtEmail.getText().toString().isEmpty())
                {
                    /*
                    Si alguno de los campos está en blanco, se le pedirá que lo rellene.
                     */
                    CrearToast crearToast = new CrearToast(RecuperarPasswordActivity.this,"Introduzca todos los datos, por favor.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    /*
                    Petición que cogerá los datos de la base de datos.
                     */
                    webService = new WebService("select","select nombre, email from users where nombre = '"+
                            txtNombre.getText().toString()+"' and email = '"+txtEmail.getText().toString()+"'");
                    webService.execute();

                    try {
                        String info = webService.get();
                        System.out.println("SOY INFO RECUPERAR CONTRASEÑA "+info);

                        JSONArray obj = new JSONArray(info);

                        for (int i = 0; i < obj.length(); i++)
                        {
                            nombre = obj.getJSONObject(i).getString("nombre");
                            email = obj.getJSONObject(i).getString("email");
                        }


                        /*
                        Si lo que ha escrito el usuario coincide con lo que hay en la base de datos, se le mostrará en la ventana su nueva contraseña.
                         */
                        if (nombre.equals(txtNombre.getText().toString()) && email.equals(txtEmail.getText().toString()))
                        {
                            int random = new Random().nextInt(900000) + 100000;
                            System.out.println("Numero random "+random);

                            webService = new WebService("update","update users set password = "+random+" where nombre = '"+nombre+"' and email = '"+email+"' ");
                            webService.execute();

                            String data = webService.get();
                            System.out.println("soy data "+data);

                            if (data.equals("1"))
                            {
                                txtMostrarContraseña.setVisibility(View.VISIBLE);
                                txtMostrarContraseña.setText("Tu nueva contraseña es: "+random);
                            }
                            else
                            {
                                CrearToast crearToast = new CrearToast(RecuperarPasswordActivity.this,"Ha ocurrido un error. Disculpe las molestias.", Color.RED, layoutInflater);
                                Toast t = crearToast.CreacionToast();
                                t.show();
                            }

                        }
                        else
                        {
                            CrearToast crearToast = new CrearToast(RecuperarPasswordActivity.this,"Los datos introducidos no coinciden con ningún usuario.", Color.RED, layoutInflater);
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
