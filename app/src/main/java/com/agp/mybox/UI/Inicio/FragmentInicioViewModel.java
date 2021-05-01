package com.agp.mybox.UI.Inicio;

import android.app.Application;
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

import java.sql.SQLException;
import java.util.List;


public class FragmentInicioViewModel extends AndroidViewModel {
    private MyBoxRepository repository;
    private recuerdoAdapter adapter;
    //private MutableLiveData<List<Recuerdo>> mRecuerdos;
    private LiveData<List<Recuerdo>> liveRecuerdos;



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
        // TODO: borrar recursos asociados en tablas y disco
        repository.borrarRecuerdo(recuerdo);
    }

    public void favoritoON(int recuerdoId){
        repository.favoritoON(recuerdoId);
    }

    public void favoritoOFF(int recuerdoId){
        repository.favoritoOFF(recuerdoId);
    }
}