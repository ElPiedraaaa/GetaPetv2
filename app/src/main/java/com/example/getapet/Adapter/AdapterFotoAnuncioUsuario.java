package com.example.getapet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.InformacionProtectoraActivity;
import com.example.getapet.R;
import com.example.getapet.pojo.Animal;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AdapterFotoAnuncioUsuario extends PagerAdapter
{

    private ArrayList<Animal> arrayListAnimal;
    private LayoutInflater layoutInflater;
    private Context context;
    private int origen;
    private ViewPager viewPagerTimidin;
    private TextView txtUrgenciaAdopcion, txtNombreEdad, txtPaisLocalidad, txtDescripcion;
    private Button btnProtectora;
    private WebService webService;
    private ArrayList<File> arrayFileSuelto;
    private int idAnimal;

    public AdapterFotoAnuncioUsuario(ArrayList<Animal> arrayListAnimal, LayoutInflater layoutInflater, Context context, int origen) {
        this.arrayListAnimal = arrayListAnimal;
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.origen = origen;
    }

    /*
    Esta clase funciona como un recyclerView, pero como si fuera un ViewPager. Básicamente en el instantiateItem le
    meteremos los datos.
     */

    @Override
    public int getCount() {
        return arrayListAnimal.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container,  int position) {

        View view = layoutInflater.inflate(R.layout.layout_anuncio_animal_usuario, container,false);
        viewPagerTimidin = view.findViewById(R.id.viewPagerLayoutAnuncioAnimalUsuario);
        txtUrgenciaAdopcion = view.findViewById(R.id.txtUrgenciaAdopcionLayoutAnuncioAnimalUsuario);
        txtNombreEdad = view.findViewById(R.id.txtNombreEdadLayoutAnuncioAnimalUsuario);
        txtPaisLocalidad = view.findViewById(R.id.txtPaisLocalidadAnuncioAnimalUsuario);
        txtDescripcion = view.findViewById(R.id.txtDescripciónAnuncioAnimalUsuario);
        btnProtectora = view.findViewById(R.id.btnProtectoraAnuncioAnimalUsuario);
        arrayFileSuelto = new ArrayList<>();

        /*
        Si origen == 1, eso significa que venimos de una vista que no necesita del botón de protectora.

        Todo el código que hay aquí es para parsear los datos y ponerlos correctamente visibles al público.
         */

        if (origen == 1)
        {
            btnProtectora.setVisibility(View.INVISIBLE);
        }
        else
        {

        }

        System.out.println("SOY ARRAYLIST ANIMAL .SIZE "+arrayListAnimal.size());

        if (arrayListAnimal.get(position).getEstado() == 1)
        {
            txtUrgenciaAdopcion.setText("Urgente");
            txtUrgenciaAdopcion.setBackgroundColor(Color.RED);
        }
        else
        {
            txtUrgenciaAdopcion.setText("No urgente");
            txtUrgenciaAdopcion.setBackgroundColor(Color.GREEN);
        }

        /*
        Obtenemos el nombre del país
         */

        webService = new WebService("select","select nombre from pais where id ="+arrayListAnimal.get(position).getPais());
        webService.execute();

        String nombre="";
        String nombreCiudad  = "";
        try {
            String info = webService.get();
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length() ; i++)
            {
                nombre = jsonArray.getJSONObject(i).getString("nombre");
                System.out.println("SOY NOMBRE "+nombre);
            }

            /*
            Obtenemos el nombre de la ciudad
             */

            webService = new WebService("select","select nombre from ciudades where id ="+arrayListAnimal.get(position).getCiudad());
            webService.execute();

            String intel = webService.get();
            JSONArray temp = new JSONArray(intel);

            for (int i = 0; i < temp.length(); i++)
            {
                nombreCiudad = temp.getJSONObject(i).getString("nombre");
                System.out.println("SOY NOMBRE CIUDAD "+nombreCiudad);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        idAnimal = arrayListAnimal.get(position).getId();

        btnProtectora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InformacionProtectoraActivity.class);
                i.putExtra("idAnimal",idAnimal);
                context.startActivity(i);
            }
        });



        arrayFileSuelto = arrayListAnimal.get(position).getImagenes();

        AdapterFotoAnuncio adapterFotoAnuncio = new AdapterFotoAnuncio(arrayFileSuelto,context);
        viewPagerTimidin.setAdapter(adapterFotoAnuncio);

        txtPaisLocalidad.setText(nombre+" , "+nombreCiudad);
        txtNombreEdad.setText("Nombre: "+arrayListAnimal.get(position).getNombre()+" Edad: "+arrayListAnimal.get(position).getEdad());

        txtDescripcion.setMovementMethod(new ScrollingMovementMethod());
        txtDescripcion.setText(""+arrayListAnimal.get(position).getDescripcion());

        System.out.println("SOY VIEW "+view);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
