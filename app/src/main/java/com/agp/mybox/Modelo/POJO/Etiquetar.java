package com.agp.mybox.Modelo.POJO;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName ="etiquetar",primaryKeys = {"idRecuerdo", "idEtiqueta"}, foreignKeys =
        {@ForeignKey(
            entity = Recuerdo.class,
            parentColumns = "id",
            childColumns = "idRecuerdo"),
            @ForeignKey(
                    entity = Etiqueta.class,
                    parentColumns = "id",
                    childColumns = "idEtiqueta")},
        indices = {@Index(value= {"idEtiqueta"})}
)

public class Etiquetar {
    private int idRecuerdo, idEtiqueta;

    public Etiquetar(){

    }

    @Ignore
    public Etiquetar(int idRecuerdo, int idEtiqueta){
        this.idEtiqueta=idEtiqueta;
        this.idRecuerdo=idRecuerdo;
    }

    public int getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(int idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }

    public int getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(int idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }
}
