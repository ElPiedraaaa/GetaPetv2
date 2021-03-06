package com.example.getapet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.getapet.Adapter.AdapterAnuncio;
import com.example.getapet.Adapter.AdapterFotoAnuncioUsuario;
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

public class AnunciosAnimalesActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private WebService webService;
    private int tipoUser;
    private String idUsuario, especie, urgencia, pais, ciudad, rangoEdad;
    private LayoutInflater layoutInflater;
    private ArrayList<Animal> listaProvisional;
    private ArrayList<String> nombreImagenes;
    private ArrayList<File> arrayArchivos;
    private ArrayList<File> arrayFilesTemporal;
    private ArrayList<ArrayList<File>> arrayFilesDefinitivo;
    private ArrayList<String> directorios;
    private AdapterFotoAnuncioUsuario adapterAnuncioUsuario;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios_animales);

         /*
        Arreglamos el título, le damos nuestro color a la barra del título y forzamos a que la app sólo se pueda ver en vertical.
         */

        setTitle("Anuncios de Animales");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

         /*
        Encontramos IDs, iniciamos ArrayLists, y el Layout Inflater.
         */

        viewPager = findViewById(R.id.viewPagerAnunciosAnimalesActivity);

        int filtroActivo = 0;

        listaProvisional = new ArrayList<>();
        nombreImagenes = new ArrayList<>();
        directorios = new ArrayList<>();
        arrayFilesTemporal = new ArrayList<>();
        arrayFilesDefinitivo = new ArrayList<>();

        layoutInflater = getLayoutInflater();

        tipoUser = getIntent().getIntExtra("tipo_user",0);
        idUsuario = getIntent().getStringExtra("idUsuario");
        filtroActivo = getIntent().getIntExtra("Filtro",0);

        /*
        Obtenemos toda esta información si venimos de los filtros, sino no es necesario.
         */

        especie = getIntent().getStringExtra("Especie");
        urgencia = getIntent().getStringExtra("Urgencia");
        pais = getIntent().getStringExtra("Pais");
        ciudad = getIntent().getStringExtra("Ciudad");
        rangoEdad = getIntent().getStringExtra("RangoEdad");

        /*
        Si hay filtro activo, es que venimos de los filtros y hacemos esta query a la base de datos, sino hacemos la otra.
         */

        if (filtroActivo == 1)
        {
            System.out.println("Venimos de los filtros");

            webService = new WebService("select","select id, nombre, edad, pais, ciudad, descripcion, estado, tipo from animales where idOwnerActual != "+idUsuario+" and adoptado ="+0+especie+urgencia
                    +pais+ciudad+rangoEdad);
            webService.execute();
        }
        else
        {
            System.out.println("No venimos de los filtros");
            webService = new WebService("select","select id, nombre, edad, pais, ciudad, descripcion, estado, tipo from animales where idOwnerActual != "+idUsuario+" and adoptado ="+0);
            webService.execute();
        }


        /*
        En esta actividad tengo que descargarme parte de los datos de los animales, y aquí metere el setAdapter
        al View Pager.
         */

        String nombre, descripcion, imagen;
        int edad, pais, ciudad, estado,tipo, id;

        try {
            String info = webService.get();

            /*
            Si la query no ha dado nada y venimos de los filtros, es que ningún animal ha satisfacido los filtros del usuario.
             */

            if (info.equals("0") && filtroActivo == 1)
            {
                CrearToast crearToast = new CrearToast(AnunciosAnimalesActivity.this,"No tenemos animales que satisfagan sus filtros ", Color.RED, layoutInflater);
                Toast t = crearToast.CreacionToast();
                t.show();
            }
            else
            {

                /*
                Si la query no ha traído nada, se muestra un mensaje de error.
                 */

                if (info.equals("0"))
                {
                    CrearToast crearToast = new CrearToast(AnunciosAnimalesActivity.this,"Ha ocurrido un error, compruebe su conexión a Internet y disculpe las molestias.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                    System.out.println("SOY INFO "+info);
                    JSONArray jay = new JSONArray(info);

                    /*
                    Si no se cumplen las demás cosas, se obtienen los datos del animal y se crea un objeto Animal con él y se añade al ArrayList de animales.
                     */

                    for (int i = 0; i < jay.length(); i++)
                    {
                        id = jay.getJSONObject(i).getInt("id");
                        nombre = jay.getJSONObject(i).getString("nombre");
                        edad = jay.getJSONObject(i).getInt("edad");
                        pais = jay.getJSONObject(i).getInt("pais");
                        ciudad = jay.getJSONObject(i).getInt("ciudad");
                        descripcion = jay.getJSONObject(i).getString("descripcion");
                        estado = jay.getJSONObject(i).getInt("estado");
                        tipo = jay.getJSONObject(i).getInt("tipo");

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

                        /*
                    Conseguimos los nombres de las imágenes de cada animal.
                     */

                        System.out.println("SOY LISTA PROVISIONAL.GET I .GET ID "+listaProvisional.get(i).getId());
                        webService = new WebService("select","select distinct nombre_imagen from imagenesAnimal where id_animal = "+listaProvisional.get(i).getId());
                        webService.execute();

                        String xd = webService.get();
                        System.out.println("SOY XD "+xd);
                        JSONArray obj = new JSONArray(xd);

                         /*
                    Creamos el string que contendrá el nombre del directorio que tiene el directorio donde se han alojado las imágenes en el servidor. Y lo metemos en un arrayList de directorios.
                     */

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

                         /*
                    Antes hemos obtenido todas las imágenes de un animal y las hemos metido en un ArrayList, ese ArrayList se añade al Array de animales que hemos creado anteriormente.
                     */

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

                    /*
                Descargamos las imággenes
                 */

                    FTPServidorDownload ftpServidorDownload = new FTPServidorDownload(listaProvisional,directorios);
                    ftpServidorDownload.execute();

                    System.out.println("FTP SERVIDOR DOWNLOAD .GET "+ftpServidorDownload.get());

                    if (ftpServidorDownload.get().equals("ok"))
                    {
                        /*
                    Creamos el adapter y le pasamos toda la información necesaria para después darle ese Adapter al View Pager.
                     */

                        adapterAnuncioUsuario = new AdapterFotoAnuncioUsuario(listaProvisional,layoutInflater,AnunciosAnimalesActivity.this,0);
                        viewPager.setAdapter(adapterAnuncioUsuario);
                    }
                    else
                    {
                        CrearToast crearToast = new CrearToast(AnunciosAnimalesActivity.this,"Ha ocurrido un error, compruebe su conexión a Internet y disculpe las molestias.", Color.GREEN, layoutInflater);
                        Toast t = crearToast.CreacionToast();
                        t.show();
                    }
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
