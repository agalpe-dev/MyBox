package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName ="recuerdo", foreignKeys = @ForeignKey(
        entity = TipoRecuerdo.class,
        parentColumns = "id",
        childColumns = "idTipoRecuerdo")
)

public class Recuerdo {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;

    @NonNull
    @ColumnInfo(name="fecha", defaultValue = "CURRENT_TIMESTAMP")
    private long fecha;

    private String titulo;
    private String comentario;
    private int favorito;
    private long idTipoRecuerdo;

    public Recuerdo() {
    }

    @Ignore
    public Recuerdo(String titulo, long fecha, String comentario, int favorito, long idTipoRecuerdo){
        this.titulo=titulo;
        this.fecha=fecha;
        this.comentario=comentario;
        this.favorito=favorito;
        this.idTipoRecuerdo=idTipoRecuerdo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getFavorito() {
        return favorito;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
    }

    public long getIdTipoRecuerdo() {
        return idTipoRecuerdo;
    }

    public void setIdTipoRecuerdo(long idTipoRecuerdo) {
        this.idTipoRecuerdo = idTipoRecuerdo;
    }

    public long getTimestamp(){
        long t=System.currentTimeMillis()/1000;
        return t;
    }
}
