package com.example.getapet.pojo;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class HintAdapter extends ArrayAdapter<String> {

    public HintAdapter(Context theContext, int theLayoutResId, ArrayList<String> objects) {
        super(theContext, theLayoutResId, objects);
    }

    /*
    Método que usamos para obtener el ultimo dato del ArrayList de info que se mostrará en el spinner
    y mostrarlo como hint o referencia.
     */
    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
