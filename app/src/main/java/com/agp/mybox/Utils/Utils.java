package com.agp.mybox.Utils;

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
    private final String ALMACEN="box";
    private final String FOTOS="camera";
    private final String PDF="pdf";
    private final String IMAGENES="images";

    public long getTimestamp(){
        //se devuelve un timestamp en segundos
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public String timestampToFecha(long t){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formato.format(new Date(t));
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
