package com.agp.mybox.UI;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.TipoRecuerdo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Gálvez on 28/03/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class mainActivityViewModel extends AndroidViewModel {

    private Handler handler=new Handler();
    private final String APP_RUTA=getApplication().getApplicationInfo().dataDir;
    //private final String APP_RUTA=getApplication().getFilesDir().toString();
    private final String ALMACEN=APP_RUTA+"/box";
    private final String FOTOS=ALMACEN+"/camara";
    private final String PDF=ALMACEN+"/pdf";
    private final String IMAGENES=ALMACEN+"/imagen";
    private final String OTROS= ALMACEN+"/otros";
    //private final String[] RUTAS={ALMACEN,FOTOS,PDF,IMAGENES,FOTOS};

    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

    //Executor para realizar algunas funciones en otro hilo
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private MyBoxRepository mRepository;

    public mainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    /*
    * Sólo para pruebas (crear registros para poblar el RecyclerView
    public void poblar(){
        mRepository.crearRecuerdo(new Recuerdo("Título 1",1617551190,"Comentarios 1",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 2",1617551190,"Comentarios 2",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 3",1617551190,"Comentarios 3",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 4",1617551190,"Comentarios 4",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 5",1617551190,"Comentarios 5",1,1));
        mRepository.crearRecuerdo(new Recuerdo("Título 6",1617551190,"Comentarios 6",1,1));
    }
    */

    // Comprobar si existen los tipos de Recuerdo ya que se usan como clave FK para crear cualquier
    // Recuerdo. Solo se ejecutará tras la instalación de la aplicación al estar la tabla vacía.
    public void comprobarTablaTipos(){
        int total=mRepository.totalTiposRecuerdo();
        // TODO borrar todo lo que haya en la tabla antes de completarlo
        if (total==0){
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("ticket"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("factura"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("entrada"));
            mRepository.insertarTipoRecuerdo(new TipoRecuerdo("otros"));
        }
    }


    // Comprobar si existe archivo de datos para OCR en la carpeta correspondiente.
    // Si no existe, se crea carpeta y copia el archivo
    public void checkDatosOCR(){
        // Carpeta final
        File ruta=new File(getApplication().getFilesDir()+File.separator+"tesseract"+File.separator+"tessdata");
        // Archivo final
        File spa=new File(ruta,"spa.traineddata");

        // Comprobar si existe la carpeta tessaract. Si no existe se crea (primera ejecución de la aplición)
        if (!ruta.exists()){
            ruta.mkdirs();
        }

        // Comprobar si ya existe el archivo "spa.traineddata" en destino y tiene el tamaño correcto. Si es así, se finaliza.
        if (spa.exists() && spa.length()==16733751){
            return;
        }

        // Si no existe el archivo, se copia desde assets
        AssetManager am=getApplication().getAssets();
        try {
            InputStream inputStream=am.open("tesseract/spa.traineddata");
            OutputStream outputStream=new FileOutputStream(spa);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (copiarStreams(inputStream, outputStream)){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        inputStream.close();
                                        outputStream.flush();
                                        outputStream.close();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para comprobar que existen las rutas donde se almacenarán los archivos asociados
     * a los recuerdos
     */

    public void comprobarRutas(){
        // Crear nuevo hilo para la comprobación y creación de rutas
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // Comprobar si las rutas existen. si es así, se finaliza, caso contrario
                // se comprueban y crean una a una
                File rutaFotos=new File(getApplication().getFilesDir(),"box/camara");
                File rutaPdf=new File(getApplication().getFilesDir(),"box/pdf");
                File rutaImagen=new File(getApplication().getFilesDir(),"box/imagen");
                File rutaOtros=new File(getApplication().getFilesDir(),"box/otros");
                File rutaBackup=new File(getApplication().getFilesDir(),"box/backup");
                //ArrayList con las rutas (Archivos)
                ArrayList<File> rutas=new ArrayList<File>();
                rutas.add(rutaFotos);
                rutas.add(rutaPdf);
                rutas.add(rutaImagen);
                rutas.add(rutaOtros);
                rutas.add(rutaBackup);

                // Si las rutas ya existen, finaliza la ejecución
                if (rutaFotos.exists() && rutaPdf.exists() && rutaImagen.exists() && rutaOtros.exists() && rutaBackup.exists()) {
                    return;
                }

                //Recorrer array de rutas comprobando si existen y se crean en caso que existan
                for (File f:rutas){
                    if(!f.exists()) {
                        try {
                            f.mkdirs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        executor.submit(runnable);
        // No se cierra el executor para evitar errores al recargar la Activity (rotar pantalla)
        //executor.shutdown();

        }

    public void checkPreferencias() {
        if (!mPrefs.contains("OCR")){
            SharedPreferences.Editor editor=mPrefs.edit();
            editor.putBoolean("OCR", true);
            editor.commit();
        }

        if (!mPrefs.contains("Avisos")){
            SharedPreferences.Editor editor=mPrefs.edit();
            editor.putBoolean("Avisos", true);
            editor.commit();
        }
    }

    private boolean copiarStreams(InputStream inputStream, OutputStream outputStream) throws IOException{
        byte[] buffer = new byte[1024];
        int datos;
        while((datos = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, datos);
        }
        return true;
    }
}
