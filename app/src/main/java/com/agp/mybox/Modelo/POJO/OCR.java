package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "ocr", foreignKeys = @ForeignKey(
        entity = Recuerdo.class,
        parentColumns = "id",
        childColumns = "idRecuerdo")
)

public class OCR {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String texto;

    @NonNull
    private int idRecuerdo;

    public OCR(){

    }

    @Ignore
    public OCR(String texto, int idRecuerdo){
        this.texto=texto;
        this.idRecuerdo=idRecuerdo;
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
