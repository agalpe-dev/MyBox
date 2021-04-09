package com.agp.mybox.UI;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Utils.Utils;

/**
 * Created by Antonio Gálvez on 04/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class NuevoRecuerdoViewModel extends AndroidViewModel {
    private final MyBoxRepository mRepository;
    Utils utils=new Utils();

    public NuevoRecuerdoViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    public void guardar(String titulo, String comentarios, String etiquetas) {
        long fecha=utils.getTimestamp();
        //Comprobar si tiene título o poner por defecto
        if (titulo.isEmpty()) {titulo="Sin título";}

        Recuerdo recuerdo=new Recuerdo(titulo,fecha,comentarios,0,1);
        try {
            mRepository.crearRecuerdo(recuerdo);
            Toast.makeText(getApplication(),"Recuerdo guardado!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplication(),"Error! No se pudo guardar el recuerdo", Toast.LENGTH_LONG).show();
        }
    }
}
