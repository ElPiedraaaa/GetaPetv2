package com.example.getapet.pojo;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getapet.R;

/*
Clase que crea un Toast en el sitio donde se le pasen los datos.
 */

public class CrearToast
{
    private Context context;
    private String mensaje;
    private int color;
    private LayoutInflater layoutInflater;

    public CrearToast(Context context, String mensaje, int color, LayoutInflater layoutInflater) {
        this.context = context;
        this.mensaje = mensaje;
        this.color = color;
        this.layoutInflater = layoutInflater;
    }

    public CrearToast() {
    }

    public Toast CreacionToast()
    {
        View layout1 = new View(context);
        layout1 = layoutInflater.inflate(R.layout.custom_toast,
                (ViewGroup) layout1.findViewById(R.id.linearLayoutCustomToast));

        TextView text2 = layout1.findViewById(R.id.txtCustomToast);
        text2.setText(mensaje);
        text2.setTextColor(Color.WHITE);
        text2.setBackgroundColor(color);

        Toast toast2 = new Toast(context);
        toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast2.setDuration(Toast.LENGTH_SHORT );
        toast2.setView(layout1);

        return toast2;
    }
}
