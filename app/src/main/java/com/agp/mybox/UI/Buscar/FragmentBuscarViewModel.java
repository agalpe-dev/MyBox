package com.agp.mybox.UI.Buscar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public FragmentBuscarViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    public List<Recuerdo> buscador(String palabra){
        return mRepository.buscarRecuerdos(palabra);
    }
}
