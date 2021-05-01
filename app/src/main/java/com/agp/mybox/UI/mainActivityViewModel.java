package com.agp.mybox.UI;

import android.app.Application;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.POJO.TipoRecuerdo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Gálvez on 28/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class mainActivityViewModel extends AndroidViewModel {
    private final String APP_RUTA=getApplication().getApplicationInfo().dataDir;
    //private final String APP_RUTA=getApplication().getFilesDir().toString();
    private final String ALMACEN=APP_RUTA+"/box";
    private final String FOTOS=ALMACEN+"/camera";
    private final String PDF=ALMACEN+"/pdf";
    private final String IMAGENES=ALMACEN+"/images";
    private final String OTROS= ALMACEN+"/otros";
    //private final String[] RUTAS={ALMACEN,FOTOS,PDF,IMAGENES,FOTOS};


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

    public void comprobarTablaTipos(){
        int total=mRepository.totalTiposRecuerdo();
        if (total==0){
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("ticket"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("factura"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("entrada"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("otros"));
        }
    }

    /**
     * Método para comprobar que existen las rutas donde se almacenarán los archivos asociados
     * a los recuerdos
     */

    public void comprobarRutas(){
        // Crear nuevo hilo para la comprobación y creación de rutas
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // Comprobar si las rutas existen. si es así, se finaliza, caso contrario
                // se comprueban y crean una a una
                File rutaFotos=new File(getApplication().getFilesDir(),"box/camara");
                File rutaPdf=new File(getApplication().getFilesDir(),"box/pdf");
                File rutaImagen=new File(getApplication().getFilesDir(),"box/imagen");
                File rutaOtros=new File(getApplication().getFilesDir(),"box/otros");
                //ArrayList con las rutas (Archivos)
                ArrayList<File> rutas=new ArrayList<File>();
                rutas.add(rutaFotos);
                rutas.add(rutaPdf);
                rutas.add(rutaImagen);
                rutas.add(rutaOtros);

                // Si las rutas ya existen, finaliza la ejecución
                if (rutaFotos.exists() && rutaPdf.exists() && rutaImagen.exists() && rutaOtros.exists()) {
                    return;
                }

                //Recorrer array de rutas comprobando si existen y se crean en caso que existan
                for (File f:rutas){
                    if(!f.exists()) {
                        try {
                            f.mkdirs();
                            Log.d("ANTONIO","Creada ruta: "+f.getPath().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("ANTONIO", "Error creando "+f.getPath().toString());
                        }
                    }
                }
                Log.d("ANTONIO", "Todas las rutas creadas ok");
            }
        };
        executor.submit(runnable);
        // No se cierra el executor para evitar errores al recargar la Activity (rotar pantalla)
        //executor.shutdown();

        }
}
