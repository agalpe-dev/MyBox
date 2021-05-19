package com.agp.mybox.Modelo.POJO;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName ="recuerdo", foreignKeys = @ForeignKey(
        entity = TipoRecuerdo.class,
        parentColumns = "id",
        childColumns = "idTipoRecuerdo",
        onDelete = CASCADE),
        indices = {@Index(value= {"idTipoRecuerdo"})}

)

public class Recuerdo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @NonNull
    @ColumnInfo(name="fecha", defaultValue = "CURRENT_TIMESTAMP")
    private long fecha;

    private String titulo;
    private String comentario;
    private int favorito;
    private int idTipoRecuerdo;

    public Recuerdo() {
    }

    @Ignore
    public Recuerdo(String titulo, long fecha, String comentario, int favorito, int idTipoRecuerdo){
        this.titulo=titulo;
        this.fecha=fecha;
        this.comentario=comentario;
        this.favorito=favorito;
        this.idTipoRecuerdo=idTipoRecuerdo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

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

    public int getIdTipoRecuerdo() {
        return idTipoRecuerdo;
    }

    public void setIdTipoRecuerdo(int idTipoRecuerdo) {
        this.idTipoRecuerdo = idTipoRecuerdo;
    }

}
