package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity (tableName = "ocr", foreignKeys = @ForeignKey(
        entity = Recuerdo.class,
        parentColumns = "id",
        childColumns = "idRecuerdo",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index(value= {"idRecuerdo"})}
)

public class OCR {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String texto;

    @NonNull
    private int idRecuerdo;

    @NonNull
    public String getUriRecurso() {
        return uriRecurso;
    }

    public void setUriRecurso(@NonNull String uriRecurso) {
        this.uriRecurso = uriRecurso;
    }

    @NonNull
    private String uriRecurso;

    public OCR(){

    }

    @Ignore
    public OCR(String texto, int idRecuerdo, String uriRecurso){
        this.texto=texto;
        this.idRecuerdo=idRecuerdo;
        this.uriRecurso=uriRecurso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    @NonNull
    public String getTexto() {
        return texto;
    }

    public void setTexto(@NonNull String texto) {
        this.texto = texto;
    }

    public int getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(int idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }
}
