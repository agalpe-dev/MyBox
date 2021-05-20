package com.agp.mybox.UI.Ajustes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

public class FragmentAjustesViewModel extends AndroidViewModel {
    private Handler handler=new Handler();
    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

    public FragmentAjustesViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean checkPreferencias(String pref){
        if(mPrefs.contains(pref)){
            return mPrefs.getBoolean(pref, false);
        }
        return false;
    }

    public void setPreferencias(String pref, boolean valor){
        SharedPreferences.Editor editor =mPrefs.edit();
        editor.putBoolean(pref,valor);
        editor.commit();
    }

    public String getNArchivos(String carpeta){
        File[] files=getFilesFromDir(carpeta);
        if (files!=null){
            return Integer.toString(files.length) + " archivo(s)" ;
        }
        return "Sin archivos";
    }

    public File[] getFilesFromDir(String carpeta){
        File [] files;
        File f = new File(getApplication().getFilesDir(),"box/"+carpeta);
        if (f.exists() && f.isDirectory()){
            files=f.listFiles();
            return files;
        }
        return null;
    }

    public String getFilesSize (String carpeta){
        File [] files=getFilesFromDir(carpeta);
        AssetFileDescriptor fileDescriptor= null;
        Uri uri=null;
        float size=0;
        DecimalFormat decimalFormat=new DecimalFormat("#.##");
        if (files.length>0) {
            try {
                for (File f : files) {
                    uri = FileProvider.getUriForFile(getApplication(), "com.agp.mybox.fileprovider", f);
                    fileDescriptor = getApplication().getContentResolver().openAssetFileDescriptor(uri, "r");
                    size+=fileDescriptor.getLength();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return decimalFormat.format(size/(1024*1024))+" Mb";
        }
        return "0 Mb";
    }

    public void borrarRecursos(String carpeta) {
        if (carpeta=="todo"){
            borrarTodosRecursos();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                File[] files=getFilesFromDir(carpeta);
                if (files!=null){
                    for (File f: files){
                        try {
                            f.delete();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                // Para acceder al hilo principal y poner el Toast
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), "¡Archivos de \"" + carpeta.toUpperCase() +"\" eliminados correctamente!", Toast.LENGTH_LONG).show();
                    }
                });


            }
        }).start();

    }

    public void borrarTodosRecursos(){
        borrarRecursos("camara");
        borrarRecursos("imagen");
        borrarRecursos("pdf");
        borrarRecursos("otros");
    }
}
