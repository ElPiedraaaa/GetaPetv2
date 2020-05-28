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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.pojo.CrearToast;
import com.example.getapet.pojo.HintAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FiltroAnuncioAnimalActivity extends AppCompatActivity {

    private Spinner spinnerTipo, spinnerRangoEdades, spinnerPais, spinnerCiudad, spinnerUrgencia;
    private Button btnBuscar;
    private WebService webService, webServiceEstado, webServicePais, webServiceCiudad;
    private ArrayList<Integer> idEspecie, idEstadoArray, idPaisArray, idPaisCiudadArray;
    private ArrayList<String> tipoEspecie, urgenciaEstadoArray, nombrePaisArray, nombreCiudadArray, rangoEdades;
    private LayoutInflater layoutInflater;

    private int tipoUser;
    private String idUsuario, especieQuery, urgenciaQuery, paisQuery, ciudadQuery, rangoEdadQuery;


    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_anuncio_animal);

        spinnerTipo = findViewById(R.id.spinnerTipoFiltroAnuncioAnimalActivity);
        spinnerRangoEdades = findViewById(R.id.spinnerRangoEdadesActivity);
        spinnerPais = findViewById(R.id.spinnerPaisFiltroAnuncioAnimalActivity);
        spinnerCiudad = findViewById(R.id.spinnerCiudadFiltroAnuncioAnimalActivity);
        spinnerUrgencia = findViewById(R.id.spinnerUrgenciaFiltroAnuncioAnimalActivity);
        btnBuscar = findViewById(R.id.btnFiltroAnuncioAnimalActivity);

        idEspecie = new ArrayList<>();
        tipoEspecie = new ArrayList<>();
        idEstadoArray = new ArrayList<>();
        urgenciaEstadoArray = new ArrayList<>();
        idPaisArray = new ArrayList<>();
        nombrePaisArray = new ArrayList<>();
        nombreCiudadArray = new ArrayList<>();
        idPaisCiudadArray = new ArrayList<>();
        rangoEdades = new ArrayList<>();

        setTitle("Get A Pet: Filtro Anuncio Animal");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0099cc")));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Para arreglar el status bar.
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue));


        layoutInflater = getLayoutInflater();

        tipoUser = getIntent().getIntExtra("tipo_user",0);
        idUsuario = getIntent().getStringExtra("idUsuario");

        final Map<String,Integer> mapEspecies = new HashMap<>();
        final Map<String, Integer> mapEstado = new HashMap<>();
        final Map<String,Integer> mapPais = new HashMap<>();
        final Map<String, Integer> mapCiudad = new HashMap<>();
        final Map<String, String> mapEdades = new HashMap<>();

           /*
        Tenemos que preguntarle el tipo, un rango de edades, pais, ciudad y urgencia.
        Serán 5 spinners. Voy a necesitar también 5 mapas, ya que tendré que pasarle los datos a la pestaña de adoptar en formato int e id.
         */

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
        nombreCiudadArray.add("Escoja una ciudad, por favor.");

        HintAdapter hintAdapter2 = new HintAdapter(FiltroAnuncioAnimalActivity.this, R.layout.support_simple_spinner_dropdown_item,nombreCiudadArray);
        spinnerCiudad.setAdapter(hintAdapter2);
        spinnerCiudad.setSelection(hintAdapter2.getCount());

        HintAdapter hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,tipoEspecie);
        spinnerTipo.setAdapter(hintAdapter);
        spinnerTipo.setSelection(hintAdapter.getCount());

        hintAdapter = new HintAdapter(this, R.layout.support_simple_spinner_dropdown_item,urgenciaEstadoArray);
        spinnerUrgencia.setAdapter(hintAdapter);
        spinnerUrgencia.setSelection(hintAdapter.getCount());

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

                    HintAdapter hintAdapter2 = new HintAdapter(FiltroAnuncioAnimalActivity.this, R.layout.support_simple_spinner_dropdown_item,nombreCiudadArray);
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

        rangoEdades.add("0-3 años");
        mapEdades.put("0-3 años","0-3");
        rangoEdades.add("4-7 años");
        mapEdades.put("4-7 años","4-7");
        rangoEdades.add("8-11 años");
        mapEdades.put("8-11 años","8-11");
        rangoEdades.add("12-15 años");
        mapEdades.put("12-15 años","12-15");
        rangoEdades.add("16-19 años");
        mapEdades.put("16-19 años","16-19");
        rangoEdades.add("20-23 años");
        mapEdades.put("20-23 años","20-23");
        rangoEdades.add("Escoja una edad, por favor.");

        hintAdapter2 = new HintAdapter(FiltroAnuncioAnimalActivity.this, R.layout.support_simple_spinner_dropdown_item,rangoEdades);
        spinnerRangoEdades.setAdapter(hintAdapter2);
        spinnerRangoEdades.setSelection(hintAdapter2.getCount());

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerTipo.getSelectedItem().toString().equals("Escoja una especie, por favor.") &&
                spinnerPais.getSelectedItem().toString().equals("Escoja un país, por favor.") &&
                        spinnerUrgencia.getSelectedItem().toString().equals("Escoja un estado, por favor.") &&
                 spinnerRangoEdades.getSelectedItem().toString().equals("Escoja una edad, por favor."))
                {
                    CrearToast crearToast = new CrearToast(FiltroAnuncioAnimalActivity.this,"Introduzca datos.", Color.RED, layoutInflater);
                    Toast t = crearToast.CreacionToast();
                    t.show();
                }
                else
                {

                    Intent i = new Intent(FiltroAnuncioAnimalActivity.this,AnunciosAnimalesActivity.class);

                    int especie = 0, urgencia = 0, pais = 0, ciudad = 0;
                    String rangoEdad = null;

                    if(!spinnerTipo.getSelectedItem().toString().equals("Escoja una especie, por favor."))
                    {
                        especie = mapEspecies.get(spinnerTipo.getSelectedItem().toString());
                        especieQuery = " and tipo = "+especie;
                        System.out.println("ESPECIE QUERY "+especieQuery);
                    }
                    else
                    {
                        especieQuery = "";
                        System.out.println("ESPECIE QUERY "+especieQuery);
                    }

                    if(!spinnerPais.getSelectedItem().toString().equals("Escoja un país, por favor."))
                    {
                        pais = mapPais.get(spinnerPais.getSelectedItem().toString());
                        paisQuery = " and pais ="+pais;
                    }
                    else
                    {
                        paisQuery = "";
                    }

                    if(!spinnerCiudad.getSelectedItem().toString().equals("Escoja una ciudad, por favor.") || spinnerCiudad.getSelectedItem().toString().isEmpty())
                    {
                        ciudad = mapCiudad.get(spinnerCiudad.getSelectedItem().toString());
                        ciudadQuery = " and ciudad ="+ciudad;
                    }
                    else
                    {
                        ciudadQuery = "";
                    }

                    if(!spinnerUrgencia.getSelectedItem().toString().equals("Escoja un estado, por favor."))
                    {
                        urgencia = mapEstado.get(spinnerUrgencia.getSelectedItem().toString());
                        urgenciaQuery = " and estado ="+urgencia;
                    }
                    else
                    {
                        urgenciaQuery = "";
                    }

                    if(!spinnerRangoEdades.getSelectedItem().toString().equals("Escoja una edad, por favor."))
                    {
                        System.out.println("SOY SPINNER RANGO EDADES "+spinnerRangoEdades.getSelectedItem().toString());
                        System.out.println("HI "+mapEdades.get("8-11 años"));
                        rangoEdad = mapEdades.get(spinnerRangoEdades.getSelectedItem().toString());
                        System.out.println("SOY RANGO EDADES "+rangoEdad);
                        String [] rango = rangoEdad.split("-");
                        System.out.println(" and edad between "+rango[0]+" and "+rango[1]);
                        rangoEdadQuery = " and edad between "+rango[0]+" and "+rango[1];
                    }
                    else
                    {
                        rangoEdadQuery = "";
                    }

                    int filtroActivo = 1;

                    System.out.println("SOY ESPECIEQUERY "+especieQuery);
                    System.out.println("SOY URGENCIAQUERY "+urgenciaQuery);
                    System.out.println("SOY PAISQUERY "+paisQuery);
                    System.out.println("SOY CIUDADQUERY "+ciudadQuery);
                    System.out.println("SOY RANGO EDAD "+rangoEdadQuery);

                    i.putExtra("Especie", especieQuery);
                    i.putExtra("Urgencia",urgenciaQuery);
                    i.putExtra("Pais", paisQuery);
                    i.putExtra("Ciudad", ciudadQuery);
                    i.putExtra("RangoEdad", rangoEdadQuery);
                    i.putExtra("Filtro", filtroActivo);
                    i.putExtra("idUsuario", idUsuario);
                    startActivity(i);
                }
            }
        });

    }
}
