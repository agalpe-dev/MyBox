package com.agp.mybox.Modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.Etiquetar;

import java.util.List;

@Dao
public interface EtiquetarDAO {
    @Insert
    public void insertarEtiquetado(Etiquetar e);

    @Update
    public void actualizarEtiquetado(Etiquetar e);

    @Delete
    public void borrarEtiquetado(Etiquetar e);

    @Query("INSERT INTO etiquetar VALUES (:idRecuerdo, :idEtiqueta)")
    public void etiquetar (int idRecuerdo, int idEtiqueta);

    @Query("DELETE FROM etiquetar WHERE idRecuerdo = :idRecuerdo")
    public void borrarEtiquetadoRecuerdo(int idRecuerdo);

    @Query("SELECT E.etiqueta FROM etiquetar ER INNER JOIN etiqueta E ON E.id = ER.idEtiqueta WHERE ER.idRecuerdo = :idRecuerdo")
    public List<String> etiquetasDeRecuerdo(int idRecuerdo);
}
