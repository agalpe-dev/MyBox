package com.agp.mybox.Modelo.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Recurso;

import java.util.List;

@Dao
public interface RecursoDAO {
    @Insert
    public void insertarRecurso(Recurso recurso);

    @Update
    public void actualizarRecurso(Recurso recurso);

    @Delete
    public void borrarRecurso(Recurso recurso);

    @Query("SELECT * FROM recurso WHERE id = :id")
    public LiveData<List<Recurso>> listarRecurso(long id);

    @Query("SELECT * FROM recurso WHERE idRecuerdo = :id")
    public LiveData<List<Recurso>> RecursosDeRecuerdo(long id);
}
