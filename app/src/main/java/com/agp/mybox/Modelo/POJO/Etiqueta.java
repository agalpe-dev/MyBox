package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "etiqueta")
public class Etiqueta {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String etiqueta;

    public Etiqueta(){

    }

    @Ignore
    public Etiqueta(String etiqueta){
        this.etiqueta=etiqueta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id; }

    @NonNull
    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(@NonNull String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
