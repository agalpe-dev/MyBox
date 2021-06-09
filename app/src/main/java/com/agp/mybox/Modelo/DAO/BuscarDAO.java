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
    @Query("SELECT DISTINCT RE.* FROM recuerdo RE, recurso RO " +
            "WHERE (RE.titulo like :palabra OR RE.comentario like :palabra OR RO.uri like :palabra) " +
            "AND RE.id = RO.idRecuerdo " +
            "UNION " +
            "SELECT DISTINCT RE.* FROM recuerdo RE, ocr mOCR " +
            "WHERE mOCR.texto LIKE :palabra " +
            "AND RE.id = mOCR.idRecuerdo")
    public List<Recuerdo> buscarRecuerdos(String palabra);
*/
    @Query("SELECT DISTINCT R.* FROM recuerdo R " +
            "LEFT JOIN tiporecuerdo TR ON R.idTipoRecuerdo = TR.id " +
            "LEFT JOIN etiquetar EE ON R.id = EE.idRecuerdo " +
            "LEFT JOIN etiqueta E ON EE.idEtiqueta = E.id " +
            "LEFT JOIN recurso RO ON RO.id = R.id " +
            "LEFT JOIN ocr mOCR ON mOCR.idRecuerdo = R.id " +
            "WHERE R.titulo LIKE :palabra OR R.comentario LIKE :palabra " +
            "OR TR.tiporecuerdo LIKE :palabra " +
            "OR RO.uri LIKE :palabra " +
            "OR mOCR.texto LIKE :palabra " +
            "OR E.etiqueta LIKE :palabra")
    public List<Recuerdo> buscarRecuerdos(String palabra);
}
