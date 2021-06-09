package com.agp.mybox.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private List<Recuerdo> mListaRecuerdos =new ArrayList<>();
    Utils utils=new Utils();
    private MutableLiveData<Integer> favoritoOn= new MutableLiveData<>();
    private MutableLiveData<Integer> favoritoOff=new MutableLiveData<>();
    private ItemClickListener clickListener;
    Context context;

    public recuerdoAdapter(ItemClickListener clickListener, Context context) {
        this.clickListener = clickListener;
        this.context=context;
    }

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
        Recuerdo recuerdo= mListaRecuerdos.get(position);

        holder.titulo.setText(recuerdo.getTitulo());
        holder.comentarios.setText(recuerdo.getComentario());
        holder.fecha.setText(utils.timestampToFecha(recuerdo.getFecha()));
        holder.idRegistro.setText(Integer.toString(recuerdo.getId()));
        int tipoRecuerdo = recuerdo.getIdTipoRecuerdo();
        // Poner color fondo en tarjeta según tipo de recuerdo
        // TODO -> Revisar, usar texto de recuerdo y buscar el código en base de datos. no siempre va a ser 1, 2, 3, 4
        switch (tipoRecuerdo){
            case 1:
                //holder.itemView.setBackgroundColor(Color.parseColor("#D8E2EB"));
                holder.tarjeta.setCardBackgroundColor(Color.parseColor("#D8E2EB"));
                break;
            case 2:
                // holder.itemView.setBackgroundColor(Color.parseColor("#F0BCAE")); -> efecto secundario tarjeta pierde round corner
                holder.tarjeta.setCardBackgroundColor(Color.parseColor("#F0BCAE"));
                break;
            case 3:
                //holder.itemView.setBackgroundColor(Color.parseColor("#D6E9CF"));
                holder.tarjeta.setCardBackgroundColor(Color.parseColor("#D6E9CF"));
                break;
            case 4:
                //holder.itemView.setBackgroundColor(Color.parseColor("#E5D9E4"));
                holder.tarjeta.setCardBackgroundColor(Color.parseColor("#E5D9E4"));
                break;

        }
        // Si el valor de favorito en el Recuerdo es 1, es favorito.
        // Se activa el checkbox o no
        if(recuerdo.getFavorito()==1){
            holder.bFavorito.setChecked(true);
        }else{
            holder.bFavorito.setChecked(false);
        }

        // Marcar / Desmarcar favorito
        holder.bFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recuerdo.getFavorito()==1){
                    favoritoOff.setValue(recuerdo.getId());
                    notifyItemRemoved(position);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    favoritoOn.setValue(recuerdo.getId());
                }
            }
        });

        // Listener para la pulsación de item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(recuerdo);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mListaRecuerdos ==null ? 0: mListaRecuerdos.size();
    }

    public void setmListaRecuerdos(List<Recuerdo> mListaRecuerdos){
        this.mListaRecuerdos = mListaRecuerdos;
        notifyDataSetChanged();
    }

    public LiveData<Integer> getFavoritoOn(){
        return favoritoOn;
    }

    public LiveData<Integer> getFavoritoOff(){
        return favoritoOff;
    }

    public static class recuerdoViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo, etiquetas, comentarios, fecha, idRegistro;
        private ImageView imagen;
        private ImageButton bBorrar;
        private CheckBox bFavorito;
        private CardView tarjeta;

        public recuerdoViewHolder(@NonNull View itemView){
            super(itemView);
            titulo=(TextView)itemView.findViewById(R.id.tarjetaTitulo);
            etiquetas=(TextView)itemView.findViewById(R.id.tarjetaEtiquetas);
            comentarios=(TextView)itemView.findViewById(R.id.tarjetaComentarios);
            fecha=(TextView)itemView.findViewById(R.id.tarjetaFecha);
            imagen=(ImageView)itemView.findViewById(R.id.tarjetaImagen);
            bFavorito=(CheckBox) itemView.findViewById(R.id.botonFavorito);
            idRegistro=(TextView) itemView.findViewById(R.id.txtIdRegistro);
            tarjeta=itemView.findViewById(R.id.tarjeta);

        }
    }

    public interface ItemClickListener {
        public void onItemClick(Recuerdo recuerdo);
    }
}