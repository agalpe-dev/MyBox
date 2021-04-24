package com.agp.mybox.UI.Favoritos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;

import java.util.List;


public class FragmentFavoritosViewModel extends AndroidViewModel {

    private MyBoxRepository repository;
    private LiveData<List<Recuerdo>> liveRecuerdos;

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
}