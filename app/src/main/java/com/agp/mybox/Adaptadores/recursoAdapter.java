package com.agp.mybox.Adaptadores;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.agp.mybox.Modelo.Parciales.RecursoMini;
import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio Gálvez on 13/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class recursoAdapter extends RecyclerView.Adapter<recursoAdapter.recursoViewHolder> {
    private List<RecursoMini> recursos=new ArrayList<RecursoMini>();
    private Utils utils=new Utils();

    @NonNull
    @Override
    public recursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_recurso_mini,parent,false);
       return new recursoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull recursoViewHolder holder, int position) {
        RecursoMini recurso=recursos.get(position);
        Bitmap miniatura= recurso.getMiniatura();
        miniatura=utils.rotarImagen(miniatura,-270);
        if (miniatura!=null){
            holder.miniatura.setImageBitmap(miniatura);
        }
    }

    @Override
    public int getItemCount() {
       if (recursos == null){
           return 0;
       }else{
           return recursos.size();
       }
    }

    public void setRecursos(List<RecursoMini> recursos){
        this.recursos=recursos;
        notifyDataSetChanged();
    }


    public static class recursoViewHolder extends RecyclerView.ViewHolder{
        private ImageView miniatura;
        public recursoViewHolder(@NonNull View itemView) {
            super(itemView);
            miniatura=(ImageView) itemView.findViewById(R.id.imagenMini);
        }
    }
}
