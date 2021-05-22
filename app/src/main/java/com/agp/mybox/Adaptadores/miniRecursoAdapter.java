package com.agp.mybox.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agp.mybox.Modelo.Compuestos.RecursoMini;
import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio Gálvez on 13/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class miniRecursoAdapter extends RecyclerView.Adapter<miniRecursoAdapter.recursoViewHolder> {
    private List<RecursoMini> recursos=new ArrayList<RecursoMini>();
    private Utils utils=new Utils();
    Context context;

    public miniRecursoAdapter(Context context){
        this.context=context;
    }

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

        holder.miniatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir el miniRecuerso con el visor del sistema (imagen, pdf, txt)
                String tipoMime=context.getContentResolver().getType(recurso.getUri());
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(recurso.getUri(),tipoMime);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });

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

    public List<RecursoMini> getRecursos(){
        return recursos;
    }


    public static class recursoViewHolder extends RecyclerView.ViewHolder{
        private ImageView miniatura;
        public recursoViewHolder(@NonNull View itemView) {
            super(itemView);
            miniatura=(ImageView) itemView.findViewById(R.id.imagenMini);
        }
    }
}
