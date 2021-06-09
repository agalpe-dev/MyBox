package com.agp.mybox.Modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Etiqueta;

@Dao
public interface EtiquetaDAO {
    @Insert
    public void insertarEtiqueta(Etiqueta etiqueta);

    @Update
    public void actualizarEtiqueta(Etiqueta etiqueta);

    @Delete
    public void borrarEtiqueta(Etiqueta etiqueta);

    @Query("SELECT id FROM etiqueta WHERE etiqueta = :etiqueta")
    public int getEtiquetaId(String etiqueta);

    @Query("SELECT MAX(id) FROM etiqueta")
    public int ultimaEtiqueta();
}
