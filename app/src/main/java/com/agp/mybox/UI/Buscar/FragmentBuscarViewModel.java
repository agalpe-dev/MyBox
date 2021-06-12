package com.agp.mybox.UI.Buscar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;

import java.util.List;

/**
 * Created by Antonio Gálvez on 26/05/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class FragmentBuscarViewModel extends AndroidViewModel {

    private MyBoxRepository mRepository;
    private LiveData<List<Recuerdo>> listaResultados;
    private MutableLiveData<String> txtBusqueda=new MutableLiveData<>();

    public FragmentBuscarViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
        listaResultados= Transformations.switchMap(txtBusqueda, mRepository::buscarRecuerdosLive);
    }

    public List<Recuerdo> buscador(String palabra){
        return mRepository.buscarRecuerdos(palabra);
    }

    public LiveData<List<Recuerdo>> buscadorLive(){
        return listaResultados;
    }

    public void setTxtBusqueda(String palabra){
        txtBusqueda.setValue(palabra);
    }




}
