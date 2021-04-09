package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tiporecuerdo")
public class TipoRecuerdo {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String tipoRecuerdo;

    public TipoRecuerdo(){

    }

    @Ignore
    public TipoRecuerdo(String tipoRecuerdo){
        this.tipoRecuerdo=tipoRecuerdo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTipoRecuerdo() {
        return tipoRecuerdo;
    }

    public void setTipoRecuerdo(@NonNull String tipoRecuerdo) {
        this.tipoRecuerdo = tipoRecuerdo;
    }
}
