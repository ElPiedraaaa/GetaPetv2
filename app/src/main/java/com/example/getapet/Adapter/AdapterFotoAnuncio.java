package com.example.getapet.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.getapet.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterFotoAnuncio extends PagerAdapter
{
    private ArrayList<File> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;


    private ImageView imageView;


    public AdapterFotoAnuncio(ArrayList<File> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.layout_imagen_anuncio, container,false);
        imageView = view.findViewById(R.id.imageViewAnunciosFotos);

        File filin = arrayList.get(position);
        System.out.println("SOY ARRAYLIST.SIZE "+arrayList.size());

        imageView.setImageURI(Uri.fromFile(filin));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
