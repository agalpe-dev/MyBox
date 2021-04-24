package com.agp.mybox.UI;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.agp.mybox.Modelo.AppDataBase;
import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.Parciales.RecursoMini;
import com.agp.mybox.Utils.Utils;

import org.w3c.dom.ls.LSInput;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Gálvez on 04/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class NuevoRecuerdoViewModel extends AndroidViewModel {
    private ExecutorService executor= Executors.newSingleThreadExecutor();
    private final MyBoxRepository mRepository;
    private MutableLiveData<String> errorTitulo=new MutableLiveData<>();
    private MutableLiveData<List<RecursoMini>> liveRecursosMini=new MutableLiveData<List<RecursoMini>>();
    private List<RecursoMini> recursosMinis = new ArrayList<RecursoMini>();
    Utils utils=new Utils();

    // Constructor (se instancia el repositorio)
    public NuevoRecuerdoViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);

        //liveRecursosMini.setValue(recursosMinis);
    }

    /**
     * Método que crea nuevo Recuerdo y lo guarda en base de datos.
     * Se hace una comprobación del título y mediante un observable se comunica con UI en el caso
     * que esté vacío, lo que anula la creación del Recuerdo y su almacenamiento en BD
     * @param titulo
     * @param comentarios
     * @param etiquetas
     * @param tiporecuerdo
     * @return
     */
    public boolean guardar(String titulo, String comentarios, String etiquetas, String tiporecuerdo) {
        long fecha=utils.getTimestamp();

        // Comprobar título. Si está vacío se manda mensaje de error a la interfaz (LiveData)
        // y se aborta el registro en base de datos

        /* REFACTORIZADO
        if (titulo.isEmpty()) {
            errorTitulo.setValue("El título no puede estar vacío");
            return false;
        }
        else {
            errorTitulo.setValue("");
            // TODO: implementar esta consulta de forma asíncrona u observable
            int i=mRepository.getTipoRecuerdoID(tiporecuerdo);

            Recuerdo recuerdo = new Recuerdo(titulo, fecha, comentarios, 0, i);

            try {
                mRepository.crearRecuerdo(recuerdo);
                Toast.makeText(getApplication(), "Recuerdo guardado!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Error! No se pudo guardar el recuerdo", Toast.LENGTH_LONG).show();
            }
            return true;
        }*/

        if(checkTitulo(titulo)){
            int i=mRepository.getTipoRecuerdoID(tiporecuerdo);

            Recuerdo recuerdo = new Recuerdo(titulo, fecha, comentarios, 0, i);

            try {
                mRepository.crearRecuerdo(recuerdo);
                Toast.makeText(getApplication(), "Recuerdo guardado!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Error! No se pudo guardar el recuerdo", Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return false;
        }
    }

    private boolean checkTitulo(String titulo){
        if (titulo.isEmpty()) {
            errorTitulo.setValue("El título no puede estar vacío");
            return false;
        }
        else {
            errorTitulo.setValue("");
            return true;
        }
    }

    /**
     * Devuelve el texto de error en relación al título dado al recuerdo (no puede estar en vacío)
     * @return String con aviso de error
     */
    public LiveData<String> getErrorTitulo(){
        return errorTitulo;
    }

    public File crearArchivoImagen() throws IOException{
        String formato = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
        String nombreArchivo="IMG_"+formato;
        /*
        String ruta=getApplication().getApplicationInfo().dataDir+"/box/camera";
        Log.d("ANTONIO-RUTAS","getFileDir() -> " + getApplication().getFilesDir().toString());
        Log.d("ANTONIO-RUTAS","getExternalFileDir() -> " + getApplication().getExternalFilesDir(null).toString());
        Log.d("ANTONIO-RUTAS",ruta.toString());
        */
        File dir=new File(getApplication().getFilesDir()+"/box/camara");
        File archivo=File.createTempFile(nombreArchivo,".jpg",dir);
        //mImagen = archivo.getAbsolutePath();
        return archivo;
    }


    public void crearRecursoLista(Uri u){
        Bitmap bitmap= null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),u);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap mini= ThumbnailUtils.extractThumbnail(bitmap,80,80);
        recursosMinis.add(new RecursoMini(mini,u));
        liveRecursosMini.setValue(recursosMinis);
    }

    public LiveData<List<RecursoMini>> getRecursosMini(){
        return liveRecursosMini;
    }

    public boolean actualizar(String titulo, String comentarios, String etiquetas, String tiporecuerdo, int idRecuerdo) {
        if(checkTitulo(titulo)){
            long fecha=utils.getTimestamp();
            int i=mRepository.getTipoRecuerdoID(tiporecuerdo);

            Recuerdo recuerdo = new Recuerdo(titulo, fecha, comentarios, 0, i);
            recuerdo.setId(idRecuerdo);

            try {
                mRepository.actualizarRecurdo(recuerdo);
                Toast.makeText(getApplication(), "Recuerdo actualizado!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Error! No se pudo actualizar el recuerdo", Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return false;
        }
    }
}
