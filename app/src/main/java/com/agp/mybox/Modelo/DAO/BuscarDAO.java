package com.agp.mybox.Modelo.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.agp.mybox.Modelo.POJO.Recuerdo;

import java.util.List;

/**
 * Created by Antonio Gálvez on 30/05/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */

@Dao
public interface BuscarDAO {
/*
    @Query("SELECT DISTINCT RE.* FROM recuerdo RE "+
            "INNER JOIN recurso RO on (RO.idRecuerdo = RE.id) " +
            "INNER JOIN ocr OCR on (OCR.idRecuerdo = RE.id) " +
            "WHERE RE.titulo LIKE :palabra OR RE.comentario like :palabra OR OCR.texto like :palabra OR RO.uri like :palabra")
    public List<Recuerdo> buscarRecuerdos(String palabra);
*/
    @Query("SELECT DISTINCT RE.* FROM recuerdo RE, recurso RO " +
            "WHERE (RE.titulo like :palabra OR RE.comentario like :palabra OR RO.uri like :palabra) " +
            "AND RE.id = RO.idRecuerdo " +
            "UNION " +
            "SELECT DISTINCT RE.* FROM recuerdo RE, ocr mOCR " +
            "WHERE mOCR.texto LIKE :palabra " +
            "AND RE.id = mOCR.idRecuerdo")
    public List<Recuerdo> buscarRecuerdos(String palabra);
}
