package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.getapet.Adapter.AdapterAnuncio;
import com.example.getapet.ComBaseDatos.FTPServidorDownload;
import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.Animal;
import com.example.getapet.pojo.CrearToast;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class MisAnunciosActivity extends AppCompatActivity {

    private int cardAnuncio = R.layout.card_anuncio;
    private ArrayList<Animal> listaAnimalDefinitivo, listaProvisional;
    private ArrayList<String> nombreImagenes;
    private ArrayList<File> arrayArchivos;
    private int tipoUser;
    private String idUsuario ;
    private WebService webService;
    private ArrayList<String> directorios;

    private AdapterAnuncio adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private LayoutInflater layoutInflater;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_anuncios);

        /*
        Obtenemos todos los IDs e iniciamos todos los arrays.
         */

        recyclerView = findViewById(R.id.recyclerViewMisAnunciosActivity);

        listaAnimalDefinitivo = new ArrayList<>();
        listaProvisional = new ArrayList<>();
        nombreImagenes = new ArrayList<>();
        directorios = new ArrayList<>();



        layoutInflater = getLayoutInflater();
        tipoUser = getIntent().getIntExtra("tipo_user",0);
        idUsuario = getIntent().getStringExtra("idUsuario");

  /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Mis Anuncios");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        /*
        Aquí vamos a necesitar traernos todos los anuncios de animales que ha puesto el usuario de la aplicación.
        Tenemos el id del usuario, por lo tanto la primera llamada va a ser el recoger todos los animales de ese usuario.
         */

        /*
        El funcionamiento de esta página es similar a la de AnunciosAnimales y AnimalesAdoptados.
         */

        String nombre, descripcion, imagen;
        int edad, pais, ciudad, estado,tipo, id;

        webService = new WebService("select","select id, nombre, edad, pais, ciudad, descripcion, estado, tipo from animales where idOwnerActual = "+idUsuario+" and adoptado ="+0);
        webService.execute();

        try {
            String info = webService.get();

            if (info.equals("0"))
            {
                CrearToast crearToast = new CrearToast(MisAnunciosActivity.this,"No tienes ningún anuncio", Color.RED, layoutInflater);
                Toast t = crearToast.CreacionToast();
                t.show();
            }
            else
            {
                JSONArray jsonArray = new JSONArray(info);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    id = jsonArray.getJSONObject(i).getInt("id");
                    nombre = jsonArray.getJSONObject(i).getString("nombre");
                    edad = jsonArray.getJSONObject(i).getInt("edad");
                    pais = jsonArray.getJSONObject(i).getInt("pais");
                    ciudad = jsonArray.getJSONObject(i).getInt("ciudad");
                    descripcion = jsonArray.getJSONObject(i).getString("descripcion");
                    estado = jsonArray.getJSONObject(i).getInt("estado");
                    tipo = jsonArray.getJSONObject(i).getInt("tipo");

                    Animal animal = new Animal(id, nombre, edad, tipo, estado,pais,ciudad,descripcion);
                    listaProvisional.add(animal);
                }


                /*
                Ahora una vez tenemos todos los animales, debemos de obtener los nombres de las imágenes que tienen, y los metemos en nuestro Animal correspondiente
                en el ArrayList de animales.
                 */
                for (int i = 0; i < listaProvisional.size() ; i++)
                {
                    nombreImagenes = new ArrayList<>();
                    String direc = "";

                    System.out.println("SOY LISTA PROVISIONAL.GET I .GET ID "+listaProvisional.get(i).getId());
                    webService = new WebService("select","select distinct nombre_imagen from imagenesAnimal where id_animal = "+listaProvisional.get(i).getId());
                    webService.execute();

                    String xd = webService.get();
                    System.out.println("SOY XD "+xd);
                    JSONArray obj = new JSONArray(xd);

                    direc = "GAP-"+listaProvisional.get(i).getId()+"-"+listaProvisional.get(i).getNombre();
                    directorios.add(direc);
                    System.out.println("SOY NUMERO DIRECTORIOS "+directorios.size());

                    System.out.println("SOY OBJ SDFDASFASF" +obj);
                    for (int j = 0; j < obj.length(); j++)
                    {


                        imagen = obj.getJSONObject(j).getString("nombre_imagen");
                        System.out.println("SOY IMAGEN MIS ANUNCIOS ACTIVITY "+imagen);

                        nombreImagenes.add(imagen);
                    }

                    listaProvisional.get(i).setNombreImagenes(nombreImagenes);
                    System.out.println("SOY LISTAPROV.GET I .GET NOMBREIMAGENES.SIZE "+listaProvisional.get(i).getNombreImagenes().size());
                }

                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                 /*
            Para obtener las imágenes, vamos a obtener el nombre del directorio de cada animal según sus datos.
            Una vez tenemos el nombre del directorio, creamos archivos con los nombres de las imágenes, y nos descargamos cada imagen en su archivo correspondiente,
            y guardamos esos archivos en un ArrayList de archivos, que luego pasaremos al View Pager.
          */
                for (int i = 0; i < directorios.size(); i++)
                {
                    arrayArchivos = new ArrayList<>();
                    /*
                    Puedo crear aquí los directorios.
                     */

                    System.out.println("SOY DIRECTORIOS.SIZE "+directorios.size());

                    for (int j = 0; j < listaProvisional.get(i).getNombreImagenes().size(); j++)
                    {
                        String nombreImagen = listaProvisional.get(i).getNombreImagenes().get(j);

                        File supFile = File.createTempFile(nombreImagen,".jpg",storageDir);
                        arrayArchivos.add(supFile);
                        System.out.println("SOY supFILE NAME "+supFile.getName()+" supFile GET ABSOLUTE PATH "+supFile.getAbsolutePath());
                    }
                    listaProvisional.get(i).setImagenes(arrayArchivos);
                }

                FTPServidorDownload ftpServerDownload = new FTPServidorDownload(listaProvisional,directorios);
                ftpServerDownload.execute();

                if (ftpServerDownload.get().equals("ok"))
                {
                    adapter = new AdapterAnuncio(this, cardAnuncio, listaProvisional);
                    layoutManager = new GridLayoutManager(this,1); //spancount

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
