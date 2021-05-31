package com.agp.mybox.Modelo;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    private MutableLiveData<Long> rowId=new MutableLiveData<>();
    //private LiveData<List<Recuerdo>> mRecuerdo; ¿?
    //private LiveData<List<Recurso>> recursosDeRecuerdo; ¿?

    //Se declaran los DAO para tener acceso a ellos
    private RecuerdoDAO mRecuerdoDAO;
    private RecursoDAO mRecursoDAO;
    private EtiquetaDAO mEtiquetaDAO;
    private EtiquetarDAO mEtiquetarDAO;
    private OcrDAO mOcrDAO;
    private TipoRecuerdoDAO mTipoRecuerdoDAO;
    private BuscarDAO mBuscarDAO;

    //Variable local para pasar tipo int a metodos que lo necesitan para acceder a los id de los registros
    // Valorar uso para eliminar
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
        mBuscarDAO=db.getBuscarDAO();


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

    public void actualizarRecuerdo(Recuerdo recuerdo){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mRecuerdoDAO.actualizarRecuerdo(recuerdo);
              }
          });
    }

    public void crearRecurso_bg(Recurso recurso){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mRecursoDAO.insertarRecurso(recurso);
              }
          });
    }

    public void crearRecurso(Recurso recurso){
          mRecursoDAO.insertarRecurso(recurso);
    }

    public void borrarRecurso(Recurso recurso){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mRecursoDAO.borrarRecurso(recurso);
              }
          });
    }

    public void borrarRecursoId(int id){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mRecursoDAO.borrarRecursoId(id);
              }
          });
    }

    public void borrarOCRdeRecuerdo (int idrecuerdo){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mOcrDAO.borrarOCRdeRecuerdo(idrecuerdo);
              }
          });
    }

    public void borrarOCRdeUri(String uriRecurso){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {

              }
          });
    }

    public int getLastIdRecuerdo(){
          return mRecuerdoDAO.getLastId();
    }

    public int getLastIdRecurso(){
          return mRecursoDAO.getLastId();
    }

    public void borrarRecuerdo (Recuerdo recuerdo){
        mRecuerdoDAO.borrarRecuerdo(recuerdo);
    }

    public int getTipoRecuerdoID(String tiporecuerdo){
          return mTipoRecuerdoDAO.getTipoRecuerdoID(tiporecuerdo);
    }

    public String getTipoRecuerdoPorId(int id){
          return mTipoRecuerdoDAO.getTipoRecuerdoPorId(id);
    }

    public void insertarTipoRecuerdo(TipoRecuerdo tipoRecuerdo){
          //mTipoRecuerdoDAO.insertarTipoRecuerdo(tipoRecuerdo);
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTipoRecuerdoDAO.insertarTipoRecuerdo(tipoRecuerdo);
            }
        });
    }

    public int totalTiposRecuerdo(){
          return mTipoRecuerdoDAO.contar();
    }

    public int getIdRecuerdoPorFecha(long fecha){
          return mRecuerdoDAO.recuerdoPorFecha(fecha);
    }

    public void favoritoON(int idRecuerdo){
          mRecuerdoDAO.favoritoON(idRecuerdo);
    }

    public void favoritoOFF(int idRecuerdo){
          mRecuerdoDAO.favoritoOFF(idRecuerdo);
    }

    public LiveData<List<Recuerdo>> leerRecuerdosPorTipo(int tipo){
          return mRecuerdoDAO.leerRecuerdosPorTipo(tipo);
    }

    public LiveData<List<Recurso>> RecursosDeRecuerdo(int idRecuerdo){
          return mRecursoDAO.RecursosDeRecuerdo(idRecuerdo);
    }

    public void crearOCR(OCR ocr){
          databaseWriteExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  mOcrDAO.insertarOCR(ocr);
              }
          });
    }

    public List<Recuerdo> buscarRecuerdos(String palabra){
          return mBuscarDAO.buscarRecuerdos(palabra);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

