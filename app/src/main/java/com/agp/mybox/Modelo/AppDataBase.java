package com.agp.mybox.Modelo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.agp.mybox.Modelo.DAO.*;
import com.agp.mybox.Modelo.POJO.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Gálvez on 29/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
@Database(entities = {Etiqueta.class, Etiquetar.class,
        OCR.class, Recuerdo.class, Recurso.class, TipoRecuerdo.class}, version=2, exportSchema = true)
public abstract class AppDataBase extends RoomDatabase {
    public abstract RecuerdoDAO getRecuerdoDAO();
    public abstract RecursoDAO getRecursoDAO();
    public abstract EtiquetaDAO getEtiquetaDAO();
    public abstract EtiquetarDAO getEtiquetarDAO();
    public abstract OcrDAO getOcrDAO();
    public abstract TipoRecuerdoDAO getTipoRecuerdoDAO();

    /**
    public RecuerdoDAO recuerdoDAO;
    public RecursoDAO recursoDAO;
    public EtiquetaDAO etiquetaDAO;
    public EtiquetarDAO etiquetarDAO;
    public OcrDAO ocrDAO;
    public TipoRecuerdoDAO tipoRecuerdoDAO;
    */
    private static volatile AppDataBase INSTANCE;
    private static final int N_HILOS=4;
    static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(N_HILOS);

    static AppDataBase getDataBase(final Context context){
        if (INSTANCE==null){
            synchronized (AppDataBase.class){
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "base_datos")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(new RoomDatabase.Callback(){
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.execSQL("INSERT INTO tiporecuerdo (tipoRecuerdo) VALUES ('ticket');");
                                            db.execSQL("INSERT INTO tiporecuerdo (tipoRecuerdo) VALUES ('factura');");
                                            db.execSQL("INSERT INTO tiporecuerdo (tipoRecuerdo) VALUES ('entrada');");
                                            db.execSQL("INSERT INTO tiporecuerdo (tipoRecuerdo) VALUES ('otros');");
                                        }
                                    });

                                }
                            })
                            .build();
                    //INSTANCE.tiposRecuerdoInicial();
                }
            }
        }
    return INSTANCE;
    }

    /*
    RoomDatabase.Callback cargaInicial=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("ticket"));
                    getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("factura"));
                    getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("entrada"));
                    getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("otros"));
                }
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            if (getTipoRecuerdoDAO().contar()!=4){
                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        getTipoRecuerdoDAO().borrar();
                        getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("ticket"));
                        getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("factura"));
                        getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("entrada"));
                        getTipoRecuerdoDAO().insertarTipoRecuerdo(new TipoRecuerdo("otros"));
                    }
                });
            }
        }
    };
*/


    private void tiposRecuerdoInicial(){
        runInTransaction(new Runnable() {
            @Override
            public void run() {
                if(getTipoRecuerdoDAO().contar()<4) {
                    getTipoRecuerdoDAO().borrar();
                    ArrayList<TipoRecuerdo> tipos = new ArrayList<TipoRecuerdo>();
                    tipos.add(new TipoRecuerdo("ticket"));
                    tipos.add(new TipoRecuerdo("factura"));
                    tipos.add(new TipoRecuerdo("entrada"));
                    tipos.add(new TipoRecuerdo("otro"));

                    for (TipoRecuerdo t : tipos) {
                        getTipoRecuerdoDAO().insertarTipoRecuerdo(t);
                    }
                }
            }
        });
    }
}
