package com.agp.mybox.UI;

import android.app.Application;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Gálvez on 28/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class mainActivityViewModel extends AndroidViewModel {
    private final String ALMACEN="/box";
    private final String FOTOS="/camera";
    private final String PDF="/pdf";
    private final String IMAGENES="/images";
    private final String RUTA=getApplication().getApplicationInfo().dataDir;

    //Executor para realizar algunas funciones en otro hilo
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private MyBoxRepository mRepository;

    public mainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    /*
    * Sólo para pruebas (crear registros para poblar el RecyclerView
    public void poblar(){
        mRepository.crearRecuerdo(new Recuerdo("Título 1",1617551190,"Comentarios 1",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 2",1617551190,"Comentarios 2",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 3",1617551190,"Comentarios 3",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 4",1617551190,"Comentarios 4",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 5",1617551190,"Comentarios 5",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 6",1617551190,"Comentarios 6",1,1));
    }
    */

    /**
     * Método para comprobar que existen las rutas donde se almacenarán los archivos asociados
     * a los recuerdos
     */

    public void comprobarRutas(){
        File dir=new File(RUTA+ ALMACEN);
        if(dir.exists()){
            Log.d("ANTONIO", "Existe "+dir.getAbsolutePath().toString());
        }else{
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    try {
                        dir.mkdir();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            executor.submit(runnable);
            Log.d("ANTONIO", "Ruta creada "+dir.getAbsolutePath().toString());
        }

    }

}
