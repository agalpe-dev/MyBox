package com.agp.mybox.Modelo.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.TipoRecuerdo;

import java.util.List;

@Dao
public interface TipoRecuerdoDAO {
    @Insert
    public void insertarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

    @Update
    public void actualizarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

    @Delete
    public void borrarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

    @Query("SELECT id FROM tiporecuerdo WHERE LOWER(tipoRecuerdo) = LOWER(:tiporecuerdo)")
    int getTipoRecuerdoID(String tiporecuerdo);

    @Query("SELECT tipoRecuerdo FROM tiporecuerdo WHERE id = :id")
    String getTipoRecuerdoPorId(int id);

    @Query("SELECT COUNT(id) FROM tiporecuerdo")
    int contar();

    @Query("DELETE FROM tiporecuerdo")
    void borrar();

}
