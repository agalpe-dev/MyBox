package com.agp.mybox.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;

/**
 * Created by Antonio Gálvez on 28/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class mainActivityViewModel extends AndroidViewModel {

    private MyBoxRepository mRepository;

    public mainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    public void poblar(){
        mRepository.crearRecuerdo(new Recuerdo("Título 1",1617551190,"Comentarios 1",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 2",1617551190,"Comentarios 2",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 3",1617551190,"Comentarios 3",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 4",1617551190,"Comentarios 4",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 5",1617551190,"Comentarios 5",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 6",1617551190,"Comentarios 6",1,1));
    }
}
