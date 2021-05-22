package com.agp.mybox.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull recursoViewHolder holder, int position) {
        Recurso recurso=recursoList.get(position);
        // Leer Recurso y crear miniatura
        // TODO - Discriminar si es bitmap, pdf u otro tipo de archivo
        Uri uri= Uri.parse(recurso.getUri());
        // Crear thumbnail según el tipo de Recurso
        String extension=getExtension(uri);
       switch (extension){
           case "jpeg":
           case "bmp":
           case "png":
           case "tiff":
           case "gif":
               Bitmap b= null;
               try {
                   b= MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
                   Bitmap miniatura= ThumbnailUtils.extractThumbnail(b,80,80);
                   miniatura=utils.rotarImagen(miniatura,-270);
                   holder.miniatura.setImageBitmap(miniatura);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               break;
           case "pdf":
               holder.miniatura.setImageDrawable(context.getDrawable(R.drawable.tipo_pdf));
               break;
           case "plain":
               holder.miniatura.setImageBitmap(textoABitmap("<TXT>",14));
               break;
           default:
               holder.miniatura.setImageBitmap(textoABitmap(extension.toUpperCase(),14));
               break;
       }


        // Listener para click en imagen. La abre en aplicación por defecto del sistema
        holder.miniatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri,"image/jpeg");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);*/
                // Abrir el miniRecuerso con el visor del sistema (imagen, pdf, txt)
                String tipoMime=context.getContentResolver().getType(Uri.parse(recurso.getUri()));
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(recurso.getUri()),tipoMime);
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

    // Obtener extensión de URI
    private String getExtension(Uri uri){
        String tipo=getTipoArchivo(uri);
        String[] s=tipo.split("/");
        String extension=s[1];
        if (extension.equals("octet-stream")){
            String ruta=uri.toString().substring(uri.toString().length()-10);
            if (ruta.contains(".")){
                extension=ruta.substring(ruta.indexOf(".")+1);
            }
        }
        return extension;
    }

    // Obtener tipo de Archivo
    public String getTipoArchivo(Uri uri){
        String tipo=context.getContentResolver().getType(uri);
        return tipo;
    }


    // Crear imagen con texto de extensión
    // https://stackoverflow.com/questions/8799290/convert-string-text-to-bitmap
    public Bitmap textoABitmap(String texto, float textoSize) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textoSize);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(texto) + 0.0f);
        int height = (int) (baseline + paint.descent() + 0.0f);

        int trueWidth = width;
        if(width>height)height=width; else width=height;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(texto, width/2-trueWidth/2, baseline, paint);
        return image;
    }
}
