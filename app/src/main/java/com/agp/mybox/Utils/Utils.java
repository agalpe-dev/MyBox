package com.agp.mybox.Utils;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Antonio Gálvez on 04/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class Utils {
    private Context context;

    public Utils(){

    }

    public Utils(Context context){
        this.context=context;
    }


    public long getTimestamp(){
        //devuelve long en milisegundos
        long t= System.currentTimeMillis();
        return t;
    }

    public String timestampToFecha(long t){
        //Declarar formato en que queremos convertir el timestampo
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //comprobar que el long está en milisegundos.
        String s=Long.toString(t);
        if (s.length()==10){
            t=TimeUnit.SECONDS.toMillis(t); //convertir segundos a milisegundos
        }
        String date=formato.format(new Date(t));
        return date;
    }

    public List<String> etiquetasLista(String etiquetasLinea){
        //separa la linea de etiquetas en palabras según las comas
        List<String> etiquetas = null;
        String[] palabras=etiquetasLinea.split(",");
        for (String p : palabras) {
            p=p.trim();
            etiquetas.add(p);
        }
        return etiquetas;
    }

    public Bitmap rotarImagen(@NonNull Bitmap bitmap, float angulo){
        Matrix matrix=new Matrix();
        matrix.preRotate(angulo);
        Bitmap nuevaImagen=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        //bitmap.recycle();
        return nuevaImagen;
    }

    public boolean copiarArchivo(InputStream in, String destino) {
        try {
            OutputStream out = new FileOutputStream(destino);
            byte[] buffer = new byte[1024];
            int largo;
            while ((largo = in.read(buffer)) != -1) {
                out.write(buffer, 0, largo);
            }
            in.close();
            out.flush();
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void borrarArchivoDisco(File archivo) throws IOException {
        // Borrar el archivo del almacenamiento de la aplicación
        try{
            archivo.delete();
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(context,"No se ha podido borrar el archivo del almacenamiento.",Toast.LENGTH_SHORT).show();
            Log.d("AGP", "Error borrar archivo disco al borrar recuerdo");
        }

    }
}
