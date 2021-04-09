package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recurso", foreignKeys = @ForeignKey(
        entity = Recuerdo.class,
        parentColumns = "id",
        childColumns = "idRecuerdo")
)
public class Recurso {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long fecha;

    private long tamano;

    @NonNull
    private String uri;

    @NonNull
    private long idRecuerdo;

    public Recurso(){

    }

    @Ignore
    public Recurso(long fecha, long tamano, String uri, long idRecuerdo){
        this.fecha=fecha;
        this.tamano=tamano;
        this.uri=uri;
        this.idRecuerdo=idRecuerdo;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public long getTamano() {
        return tamano;
    }

    public void setTamano(long tamano) {
        this.tamano = tamano;
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    public void setUri(@NonNull String uri) {
        this.uri = uri;
    }

    public long getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(long idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }
}
