package com.agp.mybox.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Antonio Gálvez on 24/05/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */

public class BackupsViewModel extends AndroidViewModel {

    private MutableLiveData<String> archivoBackup = new MutableLiveData<String>();
    private int posicion;


    public BackupsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setArchivoBackup(String archivoBackup) {
        this.archivoBackup.setValue(archivoBackup);
    }

    public LiveData<String> getArchivoBackup(){
        return archivoBackup;
    }

    public int getPosicion(){
        return posicion;
    }

    public void setPosicion(int posicion){
        this.posicion=posicion;
    }

    public void borrarArchivoBackup(String archivoBackup) {
        File f=new File(getApplication().getFilesDir()+ File.separator+"box"+File.separator+"backup",archivoBackup);
        if (f.exists()){
            f.delete();
        }
    }
}
