package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "etiqueta")
public class Etiqueta {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String etiqueta;

    public Etiqueta(){

    }

    @Ignore
    public Etiqueta(String etiqueta){
        this.etiqueta=etiqueta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {this.id = id; }

    @NonNull
    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(@NonNull String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
