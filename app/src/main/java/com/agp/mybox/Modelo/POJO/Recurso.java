package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recurso", foreignKeys = @ForeignKey(
        entity = Recuerdo.class,
        parentColumns = "id",
        childColumns = "idRecuerdo"),
        indices = {@Index(value= {"idRecuerdo"})}
)
public class Recurso {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private long fecha;

    private long tamano;

    @NonNull
    private String uri;

    @NonNull
    private int idRecuerdo;

    public Recurso(){

    }

    @Ignore
    public Recurso(long fecha, long tamano, String uri, int idRecuerdo){
        this.fecha=fecha;
        this.tamano=tamano;
        this.uri=uri;
        this.idRecuerdo=idRecuerdo;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

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

    public int getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(int idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }
}
