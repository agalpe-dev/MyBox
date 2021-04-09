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
    private long id;

    @NonNull
    private String texto;

    @NonNull
    private long idRecuerdo;

    public OCR(){

    }

    @Ignore
    public OCR(String texto, long idRecuerdo){
        this.texto=texto;
        this.idRecuerdo=idRecuerdo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTexto() {
        return texto;
    }

    public void setTexto(@NonNull String texto) {
        this.texto = texto;
    }

    public long getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(long idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }
}
