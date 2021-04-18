package com.agp.mybox.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio Gálvez on 02/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class recuerdoAdapter extends RecyclerView.Adapter<recuerdoAdapter.recuerdoViewHolder> {
    private List<Recuerdo> recuerdos=new ArrayList<>();
    Utils utils=new Utils();


    /*
    //Recibir List con los datos
    public recuerdoAdapter(List<Recuerdo> recuerdos) {
        this.recuerdos = recuerdos;

    }
    */

    @NonNull
    @Override
    public recuerdoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_recuerdo,parent,false);
        return new recuerdoViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull recuerdoViewHolder holder, int position) {
        Recuerdo recuerdo=recuerdos.get(position);

        holder.titulo.setText(recuerdo.getTitulo());
        holder.comentarios.setText(recuerdo.getComentario());
        holder.fecha.setText(utils.timestampToFecha(recuerdo.getFecha()));
        holder.idRegistro.setText(Integer.toString(recuerdo.getId()));
        // Si el valor de favorito en el Recuerdo es 1, es favorito.
        // Se activa el checkbox o no
        if(recuerdo.getFavorito()==1){
            holder.bFavorito.setChecked(true);
        }else{
            holder.bFavorito.setChecked(false);
        }
        /* TODO. Implementar el listener para la acción: ViewModel?
        // Listener para actualizar el valor de favorito
        holder.bFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/


    }

    @Override
    public int getItemCount() {
        return recuerdos==null ? 0: recuerdos.size();
    }

    public void setRecuerdos(List<Recuerdo> recuerdos){
        this.recuerdos=recuerdos;
        notifyDataSetChanged();
    }


    public static class recuerdoViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo, etiquetas, comentarios, fecha, idRegistro;
        private ImageView imagen;
        private ImageButton bBorrar;
        private CheckBox bFavorito;

        public recuerdoViewHolder(@NonNull View itemView){
            super(itemView);
            titulo=(TextView)itemView.findViewById(R.id.tarjetaTitulo);
            etiquetas=(TextView)itemView.findViewById(R.id.tarjetaEtiquetas);
            comentarios=(TextView)itemView.findViewById(R.id.tarjetaComentarios);
            fecha=(TextView)itemView.findViewById(R.id.tarjetaFecha);
            imagen=(ImageView)itemView.findViewById(R.id.tarjetaImagen);
            bFavorito=(CheckBox) itemView.findViewById(R.id.botonFavorito);
            idRegistro=(TextView) itemView.findViewById(R.id.txtIdRegistro);
            //bBorrar=(ImageButton) itemView.findViewById(R.id.botonBorrar);

            //setear eventos
            //bFavorito.setOnClickListener(this::);

        }
    }
}