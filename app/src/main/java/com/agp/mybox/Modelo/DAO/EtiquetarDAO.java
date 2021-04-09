package com.agp.mybox.Modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Etiquetar;

@Dao
public interface EtiquetarDAO {
    @Insert
    public void insertarEtiquetado(Etiquetar e);

    @Update
    public void actualizarEtiquetado(Etiquetar e);

    @Delete
    public void borrarEtiquetado(Etiquetar e);
}
