package com.agp.mybox.Modelo.POJO;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(tableName ="etiquetar",primaryKeys = {"idRecuerdo", "idEtiqueta"}, foreignKeys =
        {@ForeignKey(
            entity = Recuerdo.class,
            parentColumns = "id",
            childColumns = "idRecuerdo"),
            @ForeignKey(
                    entity = Etiqueta.class,
                    parentColumns = "id",
                    childColumns = "idEtiqueta")}
)

public class Etiquetar {
    private long idRecuerdo, idEtiqueta;

    public Etiquetar(){

    }

    @Ignore
    public Etiquetar(long idRecuerdo, long idEtiqueta){
        this.idEtiqueta=idEtiqueta;
        this.idRecuerdo=idRecuerdo;
    }

    public long getIdRecuerdo() {
        return idRecuerdo;
    }

    public void setIdRecuerdo(long idRecuerdo) {
        this.idRecuerdo = idRecuerdo;
    }

    public long getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(long idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }
}
