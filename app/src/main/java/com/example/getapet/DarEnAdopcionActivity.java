package com.example.getapet;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.Animal;
import com.example.getapet.pojo.CrearToast;
import com.example.getapet.pojo.HintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class DarEnAdopcionActivity extends AppCompatActivity {

    private EditText txtNombre, txtEdad, txtDescripcion;
    private Spinner spinnerEspecie, spinnerEstado, spinnerPais, spinnerCiudad;
    private Button btnFotos, btnConfirmarDatos, btnBorrarFotos;
    private TextView txtNumeroImagenes;
    private static final int adios = 3;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private String currentPhotoPath;
    private Uri photoURI;
    private ArrayList<File> arrayFile;
    private ArrayList<Integer> idEspecie, idEstadoArray, idPaisArray, idPaisCiudadArray;
    private ArrayList<String> tipoEspecie, urgenciaEstadoArray, nombrePaisArray, nombreCiudadArray, insertConf;
    private WebService webService, webServiceEstado, webServicePais, webServiceCiudad;
    private LayoutInflater layoutInflater;
    private int tipoUser;
    private String id, directory, idUsuario;
    private Animal animal;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dar_en_adopcion);

        // Para que la vista no se suba entera cuando le damos a escribir la descripción.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        tipoUser = getIntent().getIntExtra("tipo_user",0);
        idUsuario = getIntent().getStringExtra("idUsuario");
        System.out.println("SOY ID USUARIO "+idUsuario);

        setTitle("Dar En Adopción");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));

        txtNombre = findViewById(R.id.txtNombreDarEnAdopcionActivity);
        txtEdad = findViewById(R.id.txtEdadDarEnAdopcionActivity);
        txtDescripcion = findViewById(R.id.txtDescripcionDarEnAdopcionActivity);
        spinnerEspecie = findViewById(R.id.spinnerEspecieDarEnAdopcionActivity);
        spinnerEstado = findViewById(R.id.spinnerEstadoDarEnAdopcionActivity);
        spinnerPais = findViewById(R.id.spinnerPaisDarEnAdopcionActivity);
        spinnerCiudad = findViewById(R.id.spinnerCiudadDarEnAdopcionActivity);
        btnFotos = findViewById(R.id.btnTomarFotosDarEnAdopcionActivity);
        btnConfirmarDatos = findViewById(R.id.btnConfirmarDatosDarEnAdopcionActivity);
        txtNumeroImagenes = findViewById(R.id.txtNumeroFotosDarEnAdopcionActivity);
        btnBorrarFotos = findViewById(R.id.btnBorrarFotosDarEnAdopcionActivity);

        arrayFile = new ArrayList<>();
        idEspecie = new ArrayList<>();
        tipoEspecie = new ArrayList<>();
        idEstadoArray = new ArrayList<>();
        urgenciaEstadoArray = new ArrayList<>();
        idPaisArray = new ArrayList<>();
        nombrePaisArray = new ArrayList<>();
        nombreCiudadArray = new ArrayList<>();
        idPaisCiudadArray = new ArrayList<>();
        insertConf = new ArrayList<>();

        layoutInflater = getLayoutInflater();

        /*
        Rellenamos los spinners con los WebService y creamos unos maps que nos ayuden a gestionar los datos.
         */
        final Map<String,Integer> mapEspecies = new HashMap<>();
        final Map<String, Integer> mapEstado = new HashMap<>();
        final Map<String,Integer> mapPais = new HashMap<>();
        final Map<String, Integer> mapCiudad = new HashMap<>();

        /*
        Llamada para recoger los datos del spinner Especie
         */
        webService = new WebService("select","select id, tipo from tipoAnimal");
        webService.execute();

        /*
        Llamada para recoger los datos del spinner Estado
         */
        webServiceEstado = new WebService("select","select id, estado from estadoAnimal");
        webServiceEstado.execute();

        /*
        Llamada para recoger los datos del spinner Pais
         */

        webServicePais = new WebService("select","select id, nombre from pais");
        webServicePais.execute();

        /*
        Llamada para recoger los datos del spinner Ciudad
         */

        webServiceCiudad = new WebService("select","select id, nombre from ciudades");
        webServiceCiudad.execute();

        try {
            /*
            Recogemos la respuesta de todos los webService, los pasamos por sus respectivos for y metemos sus datos en los respectivos arraylists y creamos su respectivo map
             */
            String respuesta = webService.get();
            JSONArray jsonArray = new JSONArray(respuesta);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                    int id = jsonArray.getJSONObject(i).getInt("id");
                    idEspecie.add(id);
                    String especie = jsonArray.getJSONObject(i).getString("tipo");
                    tipoEspecie.add(especie);
                    mapEspecies.put(tipoEspecie.get(i),idEspecie.get(i));
            }

            String infoEstado = webServiceEstado.get();
            JSONArray jsonArrayEstado = new JSONArray(infoEstado);

            for (int i = 0; i < jsonArrayEstado.length(); i++)
            {
                int idEstado = jsonArrayEstado.getJSONObject(i).getInt("id");
                idEstadoArray.add(idEstado);
                String urgenciaEstado = jsonArrayEstado.getJSONObject(i).getString("estado");
                urgenciaEstadoArray.add(urgenciaEstado);
                mapEstado.put(urgenciaEstadoArray.get(i),idEstadoArray.get(i));
            }

            String respuestaPais = webServicePais.get();
            JSONArray jsonArrayPais = new JSONArray(respuestaPais);

            for (int i = 0; i < jsonArrayPais.length() ; i++) {
                int idPais = jsonArrayPais.getJSONObject(i).getInt("id");
                idPaisArray.add(idPais);
                String nombre = jsonArrayPais.getJSONObject(i).getString("nombre");
                nombrePaisArray.add(nombre);
                mapPais.put(nombrePaisArray.get(i),idPaisArray.get(i));
            }


            String respuestaCiudad = webServiceCiudad.get();
            JSONArray jsonArrayCiudad = new JSONArray(respuestaCiudad);

            for (int i = 0; i < jsonArrayCiudad.length(); i++) {
                int idPaisCiudad = jsonArrayCiudad.getJSONObject(i).getInt("id");
                idPaisCiudadArray.add(idPaisCiudad);
                String ciudadNombre = jsonArrayCiudad.getJSONObject(i).getString("nombre");
                nombreCiudadArray.add(ciudadNombre);
                mapCiudad.put(nombreCiudadArray.get(i),idPaisCiudadArray.get(i));
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        Metemos los datos en los spinners.
         */

        tipoEspecie.add("Escoja una especie, por favor.");
        urgenciaEstadoArray.add("Escoja un estado, por favor.");
        nombrePaisArray.add("Escoja un país, por favor.");

        HintAdapter hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,tipoEspecie);
        spinnerEspecie.setAdapter(hintAdapter);
        spinnerEspecie.setSelection(hintAdapter.getCount());

        hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,urgenciaEstadoArray);
        spinnerEstado.setAdapter(hintAdapter);
        spinnerEstado.setSelection(hintAdapter.getCount());

        hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,nombrePaisArray);
        spinnerPais.setAdapter(hintAdapter);
        spinnerPais.setSelection(hintAdapter.getCount());

        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                  Dentro de este método se tendrán que descargar las ciudades de los paises segun el pais
                  escogido. La informacion del spinnerCiudades variará en función a la info del pais
                  cogido.
                   */
                nombreCiudadArray = new ArrayList<>();

                int idXD = 0;
                String pais = parent.getItemAtPosition(position).toString();
                System.out.println("SOY PAIS GAP "+pais);

                if (!spinnerPais.getSelectedItem().toString().equals("Escoja un país, por favor."))
                {
                    idXD = mapPais.get(pais);
                }

                try {

                    System.out.println("SOY ID XD"+ idXD);
                    webServiceCiudad = new WebService("select","select nombre from ciudades where id_pais = "+idXD);
                    webServiceCiudad.execute();

                    String info2 = webServiceCiudad.get();
                    JSONArray jsonArray1 = new JSONArray(info2);

                    for (int i = 0; i < jsonArray1.length(); i++)
                    {
                        String ciutats = jsonArray1.getJSONObject(i).getString("nombre");
                        System.out.println("SOY CIUTATS "+ciutats);
                        nombreCiudadArray.add(ciutats);
                    }

                    nombreCiudadArray.add("Escoja una ciudad, por favor.");

                    HintAdapter hintAdapter2 = new HintAdapter(DarEnAdopcionActivity.this, R.layout.support_simple_spinner_dropdown_item,nombreCiudadArray);
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
        Pedimos permisos de cámara al usuario.
         */
        if (ActivityCompat.checkSelfPermission(DarEnAdopcionActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, adios);
            }
            int estado2 = ContextCompat.checkSelfPermission(DarEnAdopcionActivity.this, Manifest.permission.CAMERA);
            System.out.println("soy write external storage" + estado2);
        }

        /*
        Código que se ejecutará cuando se pulse el botón fotos.
         */
        btnFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Se creará un AlertBuilder donde se le dará al usuario la opción de usar la cámara para tomar una foto o la galería.
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(DarEnAdopcionActivity.this);
                builder.setMessage("Escoja Cámara si desea tomar una foto, o Galería si ya tiene las fotos hechas de su " +
                        "animal").setTitle("Elección de medio");

                /*
                Cancelar que cancelará el Alert Dialog.
                 */
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                    }
                });

                /*
                Código que se realizará cuando el usuario decida usar la cámara para tomar una foto.
                 */
                builder.setNeutralButton("Cámara", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File photoFile = null;

                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Si la fila se ha creado correctamente,
                        if (photoFile != null) {

                            photoURI = FileProvider.getUriForFile(DarEnAdopcionActivity.this,
                                    "com.example.android.Fileprovider2",
                                    photoFile);


                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(i,REQUEST_CAMERA);
                        }

                    }
                });

                builder.setPositiveButton("Galería", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File photoFile2 = null;

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                photoFile2 = createImageFile();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (photoFile2 != null)
                        {
                            photoURI = FileProvider.getUriForFile(DarEnAdopcionActivity.this,
                                    "com.example.android.Fileprovider2",
                                    photoFile2);

                            Intent intent = new Intent(Intent.ACTION_PICK,photoURI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"Select File"),SELECT_FILE);
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        btnConfirmarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Aquí le pediremos una última confirmación de datos al usuario. Si accede, crearemos el animal
                en la base de datos y enviaremos las imágenes al servidor.

                La lógica será la siguiene: insertaremos primero el animal, cogeremos el id de ese mismo animal
                para después insertar los datos de las imágenes en tabla imágenes, y por último cogeremos las imágenes de ese animal.
                 */
                for (int i = 0; i < arrayFile.size(); i++) {
                    System.out.println("ARRAYFILE.GET I.GET NAME "+arrayFile.get(i).getName());
                }
            }
        });

        btnBorrarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayFile.size() == 0)
                {
                    CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"No hay fotos que borrar.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                     arrayFile.clear();
                     txtNumeroImagenes.setText("0");
                }
            }
        });

        btnConfirmarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtNombre.getText().toString().isEmpty() || txtEdad.getText().toString().isEmpty() ||
                spinnerPais.getSelectedItem().toString().isEmpty() || spinnerPais.getSelectedItem().toString().equals("Escoja un país, por favor.") ||
                spinnerCiudad.getSelectedItem().toString().isEmpty() || spinnerCiudad.getSelectedItem().toString().equals("Escoja una ciudad, por favor.") ||
                spinnerEspecie.getSelectedItem().toString().isEmpty() || spinnerEspecie.getSelectedItem().toString().equals("Escoja una especie, por favor.") ||
                spinnerEstado.getSelectedItem().toString().isEmpty() || spinnerEstado.getSelectedItem().toString().equals("Escoja un estado, por favor.") ||
                txtDescripcion.getText().toString().isEmpty() || arrayFile.isEmpty())
                {
                    CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"Faltan datos por introducir.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {
                      /*
                Antes de eso comprobamos que el usuario haya escrito todos los datos.
                Cuando entremos aqui la aplicación deberá de preguntar al usuario si quiere confirmar la subida del animal. Entonces deberá
                de: Crear una entrada de animal en la base de datos, según el número de fotos que haya, crear una serie de entradas en la base
                de datos, crear una carpeta en el server donde se van a almacenar las imágenes llamada con el id del animal y con su nombre, y subir las imágenes
                introducidas por el usuario ahí.
                 */

                    AlertDialog.Builder building = new AlertDialog.Builder(DarEnAdopcionActivity.this);
                    building.setMessage("Pulse Confirmar para poner un anuncio de su animal. Pulse Cancelar si no quiere hacerlo.")
                            .setTitle("Confirmación");

                    building.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    building.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        /*
                        Aquí tengo que recoger los datos introducidos por el usuario. Eso lo primero.
                         */

                            System.out.println("SOY ID ESPECIE "+idEspecie);
                            int idEspecie = mapEspecies.get(spinnerEspecie.getSelectedItem().toString());
                            System.out.println("SOY ID ESPECIE "+idEspecie);
                            int idEstado = mapEstado.get(spinnerEstado.getSelectedItem().toString());
                            System.out.println("SOY ID ESTADO "+idEstado);
                            int idPais = mapPais.get(spinnerPais.getSelectedItem().toString());
                            System.out.println("SOY ID PAIS "+idPais);
                            int idCiudad = mapCiudad.get(spinnerCiudad.getSelectedItem().toString());

                            animal  = new Animal(txtNombre.getText().toString(),Integer.parseInt(txtEdad.getText().toString()),idEspecie,idEstado,idPais,idCiudad,txtDescripcion.getText().toString());

                            webService = new WebService("insert","insert into animales(idOwnerPrevio, idOwnerActual, nombre, tipo, edad, " +
                                    " pais, ciudad, descripcion, estado, fecha, adoptado) values("+0+" , "+Integer.parseInt(idUsuario)+" , '"+animal.getNombre()+"' , "+animal.getEspecie()+" , "+animal.getEdad()+" , "+animal.getPais()
                                    +" , "+animal.getCiudad()+" , '"+animal.getDescripcion()+"' , "+animal.getEstado()+" , date(now()) , "+0+")");

                            webService.execute();

                            try {
                                String info = webService.get();

                                if (info.equals("1"))
                                {
                                    /*
                                    Primero obtenemos el id del animal inmediatamente creado.
                                     */

                                    webService = new WebService("select","select id from animales where nombre = '"+animal.getNombre()+"' and descripcion = '"+animal.getDescripcion()+"'");
                                    webService.execute();

                                    String datin = webService.get();
                                    JSONArray jsonArray = new JSONArray(datin);

                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        id = jsonArray.getJSONObject(i).getString("id");
                                        System.out.println("SOY ID DEL ANIMAL RECIEN INSERTADO "+id);
                                    }

                                    boolean okey = false;
                                    int numeroRespuestas = 0;
                                    /*
                                    Aquí es donde en la tabla imágenes, deberemos de insertar esas imágenes.
                                     */

                                    for (int i = 0; i < arrayFile.size(); i++)
                                    {
                                        webService = new WebService("insert","insert into imagenesAnimal(id_animal, nombre_imagen) values("+Integer.parseInt(id)+" , '"+arrayFile.get(i).getName()+"' )");
                                        webService.execute();

                                        String respuestilla = webService.get();
                                        insertConf.add(respuestilla);

                                        if (insertConf.get(i).equals("1"))
                                        {
                                            numeroRespuestas++;
                                        }
                                        else
                                        {
                                            CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"Ha ocurrido un error con las imágenes. ", Color.RED, layoutInflater);
                                            Toast t = crearToast.CreacionToast();
                                            t.show();
                                        }
                                    }

                                    if (arrayFile.size() == numeroRespuestas)
                                    {
                                          FTPServer ftpServer = new FTPServer();
                                          ftpServer.execute();

                                          String holiwis = ftpServer.get();
                                        System.out.println("SOY HOLIWIS "+holiwis);

                                          if (holiwis.equals("ok"))
                                          {
                                              CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"Todo ha ido bien!", Color.GREEN, layoutInflater);
                                              Toast t = crearToast.CreacionToast();
                                              t.show();

                                              btnConfirmarDatos.setVisibility(View.GONE);
                                          }
                                          else
                                          {
                                              CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"ERROR AL FINAL ", Color.RED, layoutInflater);
                                              Toast t = crearToast.CreacionToast();
                                              t.show();
                                          }

                                    }
                                }
                                else
                                {
                                    CrearToast crearToast = new CrearToast(DarEnAdopcionActivity.this,"Ha habido un error a la hora de introducir el animal.", Color.RED, layoutInflater);
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
                    });

                    AlertDialog dialog = building.create();
                    dialog.show();

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("REQUEST CODE "+requestCode+"  RESULT CODE "+resultCode +" DATA "+data);
        if (resultCode == RESULT_CANCELED)
        {

        }
        else
        {
             if (requestCode == REQUEST_CAMERA)
             {
                 /*
               En este método averiguamos la orientación que tendrá que tener la foto para que se vea correctamente.
                 */
                 int rotateImage = getCameraPhotoOrientation(DarEnAdopcionActivity.this,photoURI,
                         currentPhotoPath);

                  /*
                 El bitmap inicialmente no es mutable, y eso significa que no podemos cambiarlo de ninguna manera.
                 Con este cambio ya podemos hacerle cosas
                  */
                 BitmapFactory.Options opt = new BitmapFactory.Options();
                 opt.inMutable = true;

                 /*
        Ni idea de esta línea de código
        Vale, el getContentResolver da acceso a los content provider de la aplicación  y de otras aplicaciones.
        Le pasamos photoURI, y photoURI es algo así: content://com.example.android.Fileprovider/external_files/Pictures/JPG_21042020_1316408470982487261739185.jpg
        Podemos observar que photoURI es una dirección donde se guardarían las fotos que se acaban de tomar. por tanto, getContentResolver().
        notifyChange podría indicar que se ha producido un cambio en la carpeta donde se estan guardando los datos, y por eso le pasamos
        la nueva imagen que se ha creado.
         */
                 this.getContentResolver().notifyChange(photoURI, null);

        /*7
        Sacamos un archivo de la imagen currentPhotoPath y lo metemos en bit. Después pasamos ese bit al metodo
        saveBitmapToFile donde le daremos el tamaño  y la resolucion al archivo necesarias para poder subir el archivo al server.
        Añadimos la fila al array de archivos.
         */
                 File bit = new File(currentPhotoPath);

                 File bitmapComp = saveBitmapToFile(bit, (float) rotateImage);
                 System.out.println("SOY BITMAP COMP "+bitmapComp);
                 arrayFile.add(bitmapComp);

                 txtNumeroImagenes.setText(""+arrayFile.size());
                 System.out.println("SOY ARRAY FILE.SIZE "+arrayFile.size());
             }
             else
             {

                 try {

                     File pictureFile = null;
                     // int rotateImage = getCameraPhotoOrientation(DarEnAdopcionActivity.this, photoURI, currentPhotoPath);

                     this.getContentResolver().notifyChange(photoURI, null);

                     /*
                     Aquí cogemos la dirección del archivo, hacemos el query con el imageUri y con el filePathColumn.
                      */
                     Uri imageUri = data.getData();
                     String[] filePathColumn = {MediaStore.Images.Media.DATA};
                     Cursor cursor = DarEnAdopcionActivity.this.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                     /*
                     Movemos el cursor al principio de la database.
                      */
                     cursor.moveToFirst();

                     /*
                     Cogemos el path de las imágenes que vienen por la galería.
                      */
                     int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                     String picturePath = cursor.getString(columnIndex);
                     cursor.close();

                     /*
                     Cogemos el bitmap de la imagen desde el picturePath.
                      */
                     Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);

                     ExifInterface exif = null;
                     try {

                         /*
                         Creamos un archivo a base de la dirección que recibimos anteriormente.
                          */
                         pictureFile = new File(picturePath);
                         exif = new ExifInterface(pictureFile.getAbsolutePath());
                         System.out.println("SOY PICTURE FILE.GET ABSOLUTE PATH "+pictureFile.getAbsolutePath());
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                     int orientation = ExifInterface.ORIENTATION_NORMAL;

                     if (exif != null)
                         orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                     switch (orientation) {
                         case ExifInterface.ORIENTATION_ROTATE_90:
                             loadedBitmap = rotateBitmap(loadedBitmap, 90);
                             break;
                         case ExifInterface.ORIENTATION_ROTATE_180:
                             loadedBitmap = rotateBitmap(loadedBitmap, 180);
                             break;

                         case ExifInterface.ORIENTATION_ROTATE_270:
                             loadedBitmap = rotateBitmap(loadedBitmap, 270);
                             break;
                     }

                     System.out.println("IMAGE URI METODO NUEVO "+imageUri);

                     OutputStream os = new BufferedOutputStream(new FileOutputStream(pictureFile));
                     loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, os);
                     os.flush();
                     os.close();

                     arrayFile.add(pictureFile);

                     txtNumeroImagenes.setText(""+arrayFile.size());

                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

                 System.out.println("SOY ARRAY FILE.SIZE "+arrayFile.size());
             }
        }
    }

    /*
    Métodos para manipular las fotos y los archivos de estas
     */

    /*
   Para arreglar la orientación de la foto.
    */
    public int getCameraPhotoOrientation (Context c, Uri uri ,String imagePath){
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            System.out.println("ORIENTATION "+orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

     /*
    Método que crea el archivo donde se guardará la imagen. Se le da un nombre, un directorio y un sufijo.
    También guardamos el path de la imagen y le damos el return.
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createImageFile() throws IOException {
        // Creamos un timeStamp y despues lo concatenamos en otro string, que será el nombre de la fila.
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp;
        /*
        En storageDir cogemos el directorio donde guardaremos las fotos que se hagan. Será un directorio
        más oculto que la galeria tipica del usuario.
         */
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        /*/
        Creamos la fila temporal con todos esos datos.
         */
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir     /* directory */
        );

        // Obtenemos el path de la imagen
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("SOY CURRENT PHOTO PATH "+currentPhotoPath);
        return image;
    }

    /*
        Le pasamos un archivo file con una rotación para sacar un archivo con la resolución
        y el tamaño necesarios para ser enviado al servidor.
         */
    public File saveBitmapToFile(File file, Float rotation1){
        try {

            System.out.println("SAVE BITMAP TO FILE FILE NADA MÁS LLEGAR "+file);
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            //Nos permite llamar al bitmap sin tener que reservar memoria para sus píxeles
            o.inJustDecodeBounds = true;
            o.inMutable = true;
            o.inSampleSize = 3;
            // factor of downsizing the image

            /*
            las siguientes tres lineas de código pasan el archivo que recibe el metodo a un fileinputstream,
            que su funcion es leer bytes de un archivo, más concretamente una imagen.
            De ahí usamos el decodeStream, que convierte el inputStream en un bitmap con las opciones escritas anteriormente.
            Y cerramos el inputStream.
             */
            FileInputStream inputStream = new FileInputStream(file);
            System.out.println("SAVE BITMAP FILE PRIMER INPUT STREAM "+inputStream);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=250;

            System.out.println("SOY O.OUTWIDTH "+o.outWidth);
            System.out.println("SOY O.OUTHEIGHT "+o.outHeight);

            // Find the correct scale value. It should be the power of 2.
            /*/
            Aquí tratamos de escalar la imagen a su valor ya definitivo. outWidth y outHeight nos dará el width y hei ght de
            la imagen antes de que se aplique ningun scale.
             */
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE)
            {
                scale *= 3;
            }

            /*
            Mismo proceso que antes, obtenemos un bitmap con las nuevas opciones, en este caso el sampleSize
            será scale.
             */
            System.out.println("SOY SCALE "+scale);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);
            System.out.println("SAVE BITMAP FILE SEGUNDO  INPUT STREAM "+inputStream);

            /*/
            Nos falta un detalle, y es que necesitamos cambiar la rotación de la imagen. Con Matrix conseguimos
            eso fácilmente.
             */
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation1);

            /*
            Convertimos el inputStream en bitmap como antes y creamos un nuevo bitmap que es exactamente igual al que acabamos
            de obtener pero con nuestra rotacion.
             */
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            System.out.println("SOY SELECTEDBITMAP WIDTH "+selectedBitmap.getWidth());
            System.out.println("SOY SELECTEDBITMAP HEIGHT "+selectedBitmap.getHeight());
            selectedBitmap = Bitmap.createBitmap(selectedBitmap,0,0,selectedBitmap.getWidth(),selectedBitmap.getHeight(),matrix,true);

            // here i override the original image file
            // Reescribimos el archivo original.
            file.createNewFile();

            System.out.println("SAVE BITMAP FILE, FILE DESPUES DE SER REESCRITA "+file);

            /*/
            Para comprimir la imagen necesitamos un file output stream que reescriba los datos.
             */
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50 , outputStream);

            System.out.println("SAVE BITMAP FILE, FILE ANTES DEL FINAL "+file);
            return file;
        } catch (Exception e) {
            return file;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public class FTPServer extends AsyncTask<Void, Void, String>
    {

        private String ok = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            FTPClient client = new FTPClient();

            try {
                client.connect("192.168.1.52");
                client.login("admin","1234");
                client.setType(FTPClient.TYPE_BINARY);
                client.setPassive(true);

                String directory = "GAP-"+id+"-"+animal.getNombre();

                client.createDirectory(directory);
                client.changeDirectory(directory);

                for (int i = 0; i < arrayFile.size(); i++)
                {
                    client.upload(arrayFile.get(i));
                }

                client.disconnect(true);

                ok = "ok";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {

                if (e.getCode() == 550)
                {
                    try {
                        client.changeDirectory(directory);

                        for (int i = 0; i <arrayFile.size() ; i++)
                        {
                            client.upload(arrayFile.get(i));

                        }

                        client.disconnect(true);

                        ok = "ok";

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (FTPIllegalReplyException ex) {
                        ex.printStackTrace();
                    } catch (FTPException ex) {
                        ex.printStackTrace();
                    } catch (FTPAbortedException ex) {
                        ex.printStackTrace();
                    } catch (FTPDataTransferException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            } catch (FTPAbortedException e) {
                e.printStackTrace();
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
            }

            return ok;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
