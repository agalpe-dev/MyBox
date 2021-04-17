package com.agp.mybox.Modelo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.agp.mybox.Modelo.DAO.*;
import com.agp.mybox.Modelo.POJO.*;

import java.util.List;

import static com.agp.mybox.Modelo.AppDataBase.databaseWriteExecutor;

/**
 * Created by Antonio Gálvez on 30/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 *
 * Repositorio para centralizar acceso a datos. Por un lado se declaran los DAO de acceso a
 * Room y por otro se exponen los LiveData de acceso y ejecuta los mÃ©todos de escritura en hilos
 * independientes. De esta forma, no se interacctua con la base de datos directamente a travÃ©s de
 * los respetivos DAOs sino a traves de este Repositorio.
 */
public class MyBoxRepository {
    //Declaración de objetos LiveData que expondrán los datos
    private LiveData<List<Recuerdo>> recuerdos;
    private LiveData<List<Recurso>> recursos;
    private LiveData<List<Etiqueta>> etiquetas;
    private LiveData<List<OCR>> Ocrs;
    //private LiveData<List<Recuerdo>> mRecuerdo; ¿?
    //private LiveData<List<Recurso>> recursosDeRecuerdo; ¿?

    //Se declaran los DAO para tener acceso a ellos
    private RecuerdoDAO mRecuerdoDAO;
    private RecursoDAO mRecursoDAO;
    private EtiquetaDAO mEtiquetaDAO;
    private EtiquetarDAO mEtiquetarDAO;
    private OcrDAO mOcrDAO;
    private TipoRecuerdoDAO mTipoRecuerdoDAO;

    //Variable local para pasar tipo long a mÃ©todos que lo necesitan para acceder a los id de los registros
    private int id;


      public MyBoxRepository(Application application) {
        //Instaciación de la base de datos y DAOs
        AppDataBase db=AppDataBase.getDataBase(application);

        mRecuerdoDAO=db.getRecuerdoDAO();
        mRecursoDAO=db.getRecursoDAO();
        mEtiquetaDAO = db.getEtiquetaDAO();
        mEtiquetarDAO=db.getEtiquetarDAO();
        mOcrDAO=db.getOcrDAO();
        mTipoRecuerdoDAO=db.getTipoRecuerdoDAO();


        //Inicialización objetos datos que se expondran con el acceso a los mÃ©todos correspondientes
        //de los DAOs
        //recuerdos=mRecuerdoDAO.leerTodosRecuerdos();
        //Se declararán en el método para poder pasarles un id
        //mRecuerdo=mRecuerdoDAO.leerRecuerdo(id);
        //recursos=mRecursoDAO.listarRecurso(id);
        //recursosDeRecuerdo=mRecursoDAO.RecursosDeRecuerdo(id);
    }

    public LiveData<List<Recuerdo>> leerRecuerdo(long id){
        //return mRecuerdo;
        return mRecuerdoDAO.leerRecuerdo(id);
    }

    public LiveData<List<Recuerdo>> leerTodosRecuerdos()
    {
        recuerdos=mRecuerdoDAO.leerTodosRecuerdos();
        return recuerdos;
    }

    public LiveData<List<Recuerdo>> leerTodosFavoritos(){
          recuerdos=mRecuerdoDAO.leerTodosFavoritos();
          return recuerdos;
    }


    public void crearRecuerdo(Recuerdo recuerdo){
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRecuerdoDAO.insertarRecuerdo(recuerdo);
            }
        });
    }

    public int getTipoRecuerdoID(String tiporecuerdo){
          return mTipoRecuerdoDAO.getTipoRecuerdoID(tiporecuerdo);
    }

    public void favoritoON(int idRecuerdo){
          mRecuerdoDAO.favoritoON(idRecuerdo);
    }

    public void favoritoOFF(int idRecuerdo){
          mRecuerdoDAO.favoritoOFF(idRecuerdo);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

