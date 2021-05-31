package com.agp.mybox.UI.Inicio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agp.mybox.Adaptadores.recuerdoAdapter;
import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class FragmentInicioViewModel extends AndroidViewModel {
    private MyBoxRepository repository;
    private recuerdoAdapter adapter;
    //private MutableLiveData<List<Recuerdo>> mRecuerdos;
    private LiveData<List<Recuerdo>> liveRecuerdos;
    private Utils utils=new Utils();
    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);



    public FragmentInicioViewModel(@NonNull Application application) {
        super(application);
        repository=new MyBoxRepository(application);
        liveRecuerdos = repository.leerTodosRecuerdos();
    }

    public LiveData<List<Recuerdo>> getTodosRecuerdos(){
        return liveRecuerdos;
    }

    public LiveData<List<Recuerdo>> getRecuerdosPorTipo(int tipo) {
        liveRecuerdos=repository.leerRecuerdosPorTipo(tipo);
        return liveRecuerdos;
    }

    public int getIdTipoRecuerdo(String s){
        return repository.getTipoRecuerdoID(s);
    }

    public void borrarRecuerdo(Recuerdo recuerdo){
        // TODO: borrar recursos asociados en tablas (ya hecho al usar eliminación en Cascada) y disco
        //repository.borrarRecuerdo(recuerdo);
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

    public boolean comprobarPreferencia(String preferencia){
        boolean resultado=false;
        resultado=mPrefs.getBoolean(preferencia,false);
        return resultado;
    }

    public void favoritoON(int recuerdoId){
        repository.favoritoON(recuerdoId);
    }

    public void favoritoOFF(int recuerdoId){
        repository.favoritoOFF(recuerdoId);
    }

    public void borrarArchivo(File archivo){
        try {
            utils.borrarArchivoDisco(archivo);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}