package com.agp.mybox.Modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.TipoRecuerdo;

@Dao
public interface TipoRecuerdoDAO {
    @Insert
    public void insertarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

    @Update
    public void actualizarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

    @Delete
    public void borrarTipoRecuerdo(TipoRecuerdo tiporecuerdo);

}
