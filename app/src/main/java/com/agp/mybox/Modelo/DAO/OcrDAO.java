package com.agp.mybox.Modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.agp.mybox.Modelo.POJO.OCR;

@Dao
public interface OcrDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertarOCR(OCR ocr);

    @Update
    public void actualizarOCR(OCR ocr);

    @Delete
    public void borrarOCR(OCR ocr);

    @Query("DELETE FROM OCR WHERE idRecuerdo = :idrecuerdo")
    public void borrarOCRdeRecuerdo(int idrecuerdo);

    @Query("DELETE FROM OCR WHERE uriRecurso = :uriRecurso")
    public void borrarOCRdeUri(String uriRecurso);
}
