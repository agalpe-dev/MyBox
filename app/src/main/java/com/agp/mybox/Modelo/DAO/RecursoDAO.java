package com.agp.mybox.Modelo.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Recurso;

import java.util.List;

@Dao
public interface RecursoDAO {
    @Insert()
    public void insertarRecurso(Recurso recurso);

    @Update
    public void actualizarRecurso(Recurso recurso);

    @Delete
    public void borrarRecurso(Recurso recurso);

    @Query("DELETE FROM recurso where id = :id ")
    public void borrarRecursoId(int id);

    @Query("SELECT * FROM recurso WHERE id = :id")
    public LiveData<List<Recurso>> listarRecurso(int id);

    @Query("SELECT * FROM recurso WHERE idRecuerdo = :id")
    public LiveData<List<Recurso>> RecursosDeRecuerdo(int id);

    @Query("SELECT * FROM recurso WHERE idRecuerdo = :id")
    public List<Recurso> listaRecursosRecuerdo(int id);

    @Query("DELETE FROM recurso")
    public void borrarTodosRecursos();

    @Query("SELECT MAX (id) from recurso")
    public int getLastId();
}
