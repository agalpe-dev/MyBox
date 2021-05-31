package com.agp.mybox.UI.Favoritos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentFavoritosViewModel extends AndroidViewModel {

    private final String AUTORIDAD="com.agp.mybox.fileprovider";
    private MyBoxRepository repository;
    private LiveData<List<Recuerdo>> liveRecuerdos;
    private Utils utils=new Utils();
    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

    public FragmentFavoritosViewModel(@NonNull Application application) {
        super(application);
        repository=new MyBoxRepository(application);
        liveRecuerdos=repository.leerTodosFavoritos();
    }

    public LiveData<List<Recuerdo>> getRecuerdosFavoritos(){
        return liveRecuerdos;
    }

    public void favoritoON(int recuerdoId){
        repository.favoritoON(recuerdoId);
    }

    public void favoritoOFF(int recuerdoId){
        repository.favoritoOFF(recuerdoId);
    }

    public boolean comprobarPreferencia(String preferencia){
        boolean resultado=false;
        resultado=mPrefs.getBoolean(preferencia,false);
        return resultado;
    }

    public void borrarRecuerdo(Recuerdo recuerdo){
        // TODO: borrar recursos asociados en tablas y disco
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Recurso> listaRecursos=new ArrayList<Recurso>();
                listaRecursos=repository.listaRecursosRecuerdo(recuerdo.getId());
                repository.borrarRecuerdo(recuerdo);
                for (int i=0;i<listaRecursos.size();i++){
                    String ruta=listaRecursos.get(i).getUri();
                    File file=new File(getApplication().getFilesDir()+ File.separator+"box"+File.separator + ruta.substring(ruta.lastIndexOf("fileprovider/")+13,ruta.length()));
                    if (file.exists() && !file.isDirectory()) {
                        borrarArchivo(file);
                    }
                }
            }
        }).start();

    }

    public void borrarArchivo(File archivo){
        try {
            utils.borrarArchivoDisco(archivo);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}