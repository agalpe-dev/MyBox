package com.agp.mybox.Utils;


import android.util.Log;

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

    public long getTimestamp(){
        //long t= TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()); //opción para trabajar con segundos
        //devuelve long en milisegundos
        long t= System.currentTimeMillis();
        Log.d("TiempoSegundos",Long.toString(t));
        return t;
    }

    public String timestampToFecha(long t){
        //Declarar formato en que queremos convertir el timestampo
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //comprobar que el long está en milisegundos.
        String s=Long.toString(t);
        Log.d("TamañoT", Integer.toString(s.length()));
        if (s.length()==10){
            t=TimeUnit.SECONDS.toMillis(t); //convertir segundos a milisegundos
        }
        String date=formato.format(new Date(t));
        Log.d("TiempoFormato", date);
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

}
