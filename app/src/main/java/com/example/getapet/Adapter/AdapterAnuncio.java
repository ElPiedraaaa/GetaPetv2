package com.example.getapet.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.getapet.ComBaseDatos.FTPServidorDelete;
import com.example.getapet.ComBaseDatos.WebService;
import com.example.getapet.DarEnAdopcionActivity;
import com.example.getapet.IntroducirNuevoOwnerActivity;
import com.example.getapet.MisAnunciosActivity;
import com.example.getapet.R;
import com.example.getapet.pojo.Animal;
import com.example.getapet.pojo.CrearToast;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class AdapterAnuncio extends RecyclerView.Adapter<AdapterAnuncio.CardAnuncio>
{
    private Context context;
    private int resource;
    private ArrayList<Animal> animals;
    private ArrayList<File> filesArray;
    private WebService webService;
    private String directorio;


    public AdapterAnuncio(Context context, int resource, ArrayList<Animal> animals) {
        this.context = context;
        this.resource = resource;
        this.animals = animals;
    }

    @NonNull
    @Override
    public CardAnuncio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Se inflan los datos que recibimos de ElementoRegalo (el ViewHolder)
        View card = LayoutInflater.from(context).inflate(resource,null);
        CardAnuncio cardAnuncio = new CardAnuncio(card);
        return cardAnuncio;
    }

    @Override
    public void onBindViewHolder(@NonNull final CardAnuncio holder, int position) {

        filesArray = new ArrayList<>();
        /*
        Ponemos el estado del animal.
         */

        if (animals.get(position).getEstado() == 1)
        {
            holder.txtUrgenciaEstado.setText("Urgente");
            holder.txtUrgenciaEstado.setBackgroundColor(Color.RED);
        }
        else
        {
            holder.txtUrgenciaEstado.setText("No urgente");
            holder.txtUrgenciaEstado.setBackgroundColor(Color.GREEN);
        }

        /*
        Obtenemos el nombre del país
         */

        webService = new WebService("select","select nombre from pais where id ="+animals.get(position).getPais());
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

            webService = new WebService("select","select nombre from ciudades where id ="+animals.get(position).getCiudad());
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


        filesArray = animals.get(position).getImagenes();
        System.out.println("FILESARRAY.SIZE FUERA DEL RUNNABLE "+filesArray.size());
        AdapterFotoAnuncio adapterFotoAnuncio = new AdapterFotoAnuncio(filesArray,context);
        holder.viewPager.setAdapter(adapterFotoAnuncio);
        holder.txtPaisLocalidad.setText(nombre+" , "+nombreCiudad);

        holder.txtNombreEdad.setText("Nombre: "+animals.get(position).getNombre()+" Edad: "+animals.get(position).getEdad());

        holder.txtDescripcion.setMovementMethod(new ScrollingMovementMethod());
        holder.txtDescripcion.setText(""+animals.get(position).getDescripcion());
    }

    // El getItemCount es para saber la cantidad de elementos que vamos a necesitar
    // a.k.a listaRegalos.size(); en este caso
    @Override
    public int getItemCount() {
        return animals.size();
    }
    // Un adapter de un recyclerView es diferente a todos los demás, por tanto extiende
    // RecyclerView.Adapter<> y recibe un ViewHolder
    // El ViewHolder es la vista con los elementos ya generados

    // ViewHolder
    public class CardAnuncio extends RecyclerView.ViewHolder
    {
        private ViewPager viewPager;
        private TextView txtUrgenciaEstado, txtNombreEdad, txtPaisLocalidad, txtDescripcion;
        private Button btnConfirmarAdopción, btnBorrarAnuncio;
        private int idAnimal;


        /*
        Aqui lo suyo sería que se borraran también las imágenes del servidor, pero para eso
        creo que deberíamos de usar de otra forma el async task y tot el panorama.
         */

        public CardAnuncio(@NonNull final View itemView) {
            super(itemView);

            /*
            Obtenemos los IDs de la vista

             */
            viewPager = itemView.findViewById(R.id.viewPagerCardAnuncio);
            txtUrgenciaEstado = itemView.findViewById(R.id.txtUrgenciaAdopcionCardAnuncio);
            txtNombreEdad = itemView.findViewById(R.id.txtNombreEdadCardAnuncio);
            txtPaisLocalidad = itemView.findViewById(R.id.txtPaisLocalidadCardAnuncio);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionCardAnuncio);
            btnConfirmarAdopción = itemView.findViewById(R.id.btnConfirmarAdopcionCardAdopcion);
            btnBorrarAnuncio = itemView.findViewById(R.id.btnBorrarAnuncioConfirmarAdopcion);


            /*
            Código que se ejecutará cuando el usuario desee borrar un anuncio.
             */

            btnBorrarAnuncio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    idAnimal = animals.get(getAdapterPosition()).getId();
                    System.out.println("SOY ID ANIMAL "+idAnimal);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("Va a borrar el anuncio del animal correspondiente. Pulse Confirmar si desea borrarlo, pulse " +
                            "Cancelar para cancelar la acción.").setTitle("Ventana de confirmación");

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    /*
                    Borramos el animal de la tabla de animales y borramos también las imagenes de su tabla correspondiente y el archivo de la base de datos.
                     */

                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            webService = new WebService("delete","delete from animales where id="+idAnimal);
                            webService.execute();

                            try {
                                String info = webService.get();

                                System.out.println("SOY INFO "+info);
                                if (info.equals("1"))
                                {

                                    webService = new WebService("select","select nombre_imagen from imagenesanimal where id_animal="+idAnimal);
                                    webService.execute();

                                    ArrayList<String> arrayImagenes = new ArrayList<>();

                                    String gut = webService.get();
                                    JSONArray json = new JSONArray(gut);
                                    String nombreImagen;

                                    for (int i = 0; i < json.length(); i++)
                                    {
                                        nombreImagen = json.getJSONObject(i).getString("nombre_imagen");
                                        arrayImagenes.add(nombreImagen);
                                    }

                                    webService = new WebService("delete","delete from imagenesanimal where id_animal ="+idAnimal);
                                    webService.execute();

                                    String intel = webService.get();

                                    if (intel.equals("1"))
                                    {
                                        System.out.println("DIRECTORIOUUU "+"GAP-"+idAnimal+"-"+animals.get(getAdapterPosition()).getNombre());
                                        FTPServidorDelete FTP = new FTPServidorDelete(arrayImagenes,"GAP-"+idAnimal+"-"+animals.get(getAdapterPosition()).getNombre());
                                        FTP.execute();

                                        System.out.println("SOY FTP .GET "+FTP.get());
                                        if (FTP.get().equals("ok"))
                                        {
                                            CrearToast crearToast = new CrearToast(context,"Anuncio borrado satisfactoriamente", Color.GREEN, LayoutInflater.from(context));
                                        Toast t = crearToast.CreacionToast();
                                        t.show();

                                        itemView.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            CrearToast crearToast = new CrearToast(context,"Ha ocurrido un error en el borrado de las imágenes en el servidor ", Color.RED, LayoutInflater.from(context));
                                            Toast t = crearToast.CreacionToast();
                                            t.show();
                                        }
                                    }
                                    else
                                    {
                                        CrearToast crearToast = new CrearToast(context,"Ha ocurrido un error en el borrado de las imágenes ", Color.RED, LayoutInflater.from(context));
                                        Toast t = crearToast.CreacionToast();
                                        t.show();
                                    }
                                }
                                else
                                {
                                    CrearToast crearToast = new CrearToast(context,"Ha ocurrido un error al borrar el animal o ese animal no existe ", Color.RED, LayoutInflater.from(context));
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
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            btnConfirmarAdopción.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Aqui vamos a hacer que el usuario vaya a otra actividad donde pueda poner al adoptado.
                     */

                    idAnimal = animals.get(getAdapterPosition()).getId();
                    Intent i = new Intent(context, IntroducirNuevoOwnerActivity.class);
                    i.putExtra("id",idAnimal);
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
