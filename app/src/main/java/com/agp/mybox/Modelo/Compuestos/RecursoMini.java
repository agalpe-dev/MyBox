package com.agp.mybox.Modelo.Compuestos;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Antonio Gálvez on 17/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class RecursoMini {
    private Bitmap miniatura;
    private Uri uri;

    public RecursoMini(){

    }

    public RecursoMini(Bitmap miniatura, Uri uri){
        this.miniatura=miniatura;
        this.uri=uri;
    }

    public Bitmap getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(Bitmap minuatura) {
        this.miniatura = minuatura;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
