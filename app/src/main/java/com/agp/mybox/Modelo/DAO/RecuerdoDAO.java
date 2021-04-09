package com.agp.mybox.Modelo.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Recuerdo;

import java.util.List;

@Dao
public interface RecuerdoDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertarRecuerdo(Recuerdo recuerdo);

    @Update
    public void actualizarRecuerdo(Recuerdo recuerdo);

    @Delete
    public void borrarRecuerdo(Recuerdo recuerdo);

    @Query("SELECT * FROM recuerdo WHERE id = :id")
    LiveData<List<Recuerdo>> leerRecuerdo(long id);

    @Query("SELECT * FROM recuerdo")
    LiveData<List<Recuerdo>> leerTodosRecuerdos();


}
