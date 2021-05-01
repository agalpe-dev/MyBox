package com.agp.mybox.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio Gálvez on 01/05/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class recursoAdapter extends RecyclerView.Adapter<recursoAdapter.recursoViewHolder> {

    private List<Recurso> recursoList=new ArrayList<Recurso>();
    private Utils utils=new Utils();
    private Context context;

    public recursoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public recursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.tarjeta_recurso_mini,parent,false);
        return new recursoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull recursoViewHolder holder, int position) {
        Recurso recurso=recursoList.get(position);
        // Leer Recurso y crear miniatura
        // TODO - Discriminar si es bitmap, pdf u otro tipo de archivo
        Uri uri= Uri.parse(recurso.getUri());
        Bitmap b= null;
        try {
            b= MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
            Bitmap miniatura= ThumbnailUtils.extractThumbnail(b,80,80);
            miniatura=utils.rotarImagen(miniatura,-270);
            holder.miniatura.setImageBitmap(miniatura);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Listener para click en imagen. La abre en aplicación por defecto del sistema
        holder.miniatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri,"image/jpeg");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recursoList == null){
            return 0;
        }else {
            return recursoList.size();
        }
    }

    public void setRecursoList(List<Recurso> recursoList){
        this.recursoList= recursoList;
        notifyDataSetChanged();
    }

    public List<Recurso> getRecursoList(){
        return recursoList;
    }


    public static class recursoViewHolder extends RecyclerView.ViewHolder {
        private ImageView miniatura;
        public recursoViewHolder(@NonNull View itemView) {
            super(itemView);
            miniatura=(ImageView) itemView.findViewById(R.id.imagenMini);
        }
    }
}
