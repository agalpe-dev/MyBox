package com.agp.mybox.UI;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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

    private final String RUTA_BACKUPS = getApplication().getFilesDir()+File.separator+"box"+File.separator+"backup"+File.separator;
    private final String AUTORIDAD="com.agp.mybox.fileprovider";
    private MutableLiveData<String> archivoBackup = new MutableLiveData<String>();
    private String mArchivoBackup=null;
    private int posicion;


    public BackupsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setArchivoBackup(String archivoBackup) {
        this.archivoBackup.setValue(archivoBackup);
        this.mArchivoBackup=archivoBackup;
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

    public void exportarBackup() {
        // comprobar que hay seleccionado algun archivo para realizar la acción
        if (mArchivoBackup!=null){
            File f=new File(RUTA_BACKUPS,archivoBackup.getValue());
            Uri uri= FileProvider.getUriForFile(getApplication(),AUTORIDAD,f);
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setData(uri);
            getApplication().startActivity(intent);
        }else{
            Toast.makeText(getApplication(),"No hay archivo seleccionado.", Toast.LENGTH_LONG).show();
        }
    }
}
