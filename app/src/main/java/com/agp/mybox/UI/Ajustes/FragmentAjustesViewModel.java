package com.agp.mybox.UI.Ajustes;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FragmentAjustesViewModel extends AndroidViewModel {
    private final String AUTORIDAD="com.agp.mybox.fileprovider";
    private Utils utils=new Utils();
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
        // Carpetas de "Files" (camara, pdf, etc...)
        if (carpeta.length()<10) {
            File f = new File(getApplication().getFilesDir(), "box/" + carpeta);
            if (f.exists() && f.isDirectory()) {
                files = f.listFiles();
                return files;
            }
        }else{
            // ruta absoluta
            File f = new File(carpeta);
            if (f.exists() && f.isDirectory()) {
                files = f.listFiles();
                return files;
            }
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

    public void borrarRecursos(String carpeta, boolean avisos) {
        if (carpeta=="todo"){
            borrarTodosRecursos();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                File[] files=getFilesFromDir(carpeta);
                if (files!=null && files.length>0){
                    for (File f: files){
                        try {
                            f.delete();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    // Para acceder al hilo principal y poner el Toast si hay que mostrar avisos
                    if (avisos) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "Archivos de \"" + carpeta.toUpperCase() + "\" eliminados correctamente", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                // nada que borrar
                else{
                    if (avisos) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "¡No hay archivos que borrar!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }).start();

    }

    public void borrarTodosRecursos(){
        borrarRecursos("camara",false);
        borrarRecursos("imagen",false);
        borrarRecursos("pdf", false);
        borrarRecursos("otros", false);
        Toast.makeText(getApplication(),"¡Todos los arhivos borrados!", Toast.LENGTH_LONG).show();
    }

    public String  backupArchivos() {
        final int BUFFER = 2048;
        String nombreZip="bk_"+Long.toString(utils.getTimestamp())+".zip";
        File output = new File(getApplication().getFilesDir()+"/box/backup/"+nombreZip);

        // Añadir todos los archivos de todas las carpetas de box
        List<File> listaArchivos=new ArrayList<File>(Arrays.asList(getFilesFromDir("camara")));
        listaArchivos.addAll(Arrays.asList(getFilesFromDir("imagen")));
        listaArchivos.addAll(Arrays.asList(getFilesFromDir("pdf")));
        listaArchivos.addAll(Arrays.asList(getFilesFromDir("otros")));

        // Añadir archivo de base de datos
        //listaArchivos.add(getApplication().getDatabasePath("base_datos"));
        listaArchivos.addAll(Arrays.asList(getFilesFromDir(getApplication().getDataDir()+File.separator+"databases")));

        BufferedInputStream bufferedInputStream=null;
        ZipOutputStream zipOutputStream= null;
        try {
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(output)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Error: No se pudo crear la copia de seguridad.";
        }

        try {
            byte [] datos=new byte[BUFFER];
            for (File f: listaArchivos){
                FileInputStream fileInputStream=new FileInputStream(f);
                bufferedInputStream=new BufferedInputStream(fileInputStream,BUFFER);

                String ruta=f.getAbsolutePath();
                String rutaEnZip;

                try {
                    // Convertir rutas absolutas en relativas a /box filtrando el caso de la base de datos
                    if (ruta.contains("databases")){
                        rutaEnZip=ruta.substring(ruta.lastIndexOf("databases"));
                    } else {
                        rutaEnZip = ruta.substring(ruta.lastIndexOf("/box/") + 5);
                    }
                    ZipEntry zipEntry=new ZipEntry(rutaEnZip);
                    zipOutputStream.putNextEntry(zipEntry);

                    int contador;
                    while ((contador=bufferedInputStream.read(datos,0,BUFFER))!=-1){
                        zipOutputStream.write(datos,0,contador);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error: No se pudo crear la copia de seguridad.";
                }
                finally {
                    bufferedInputStream.close();
                }
                    }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: No se pudo crear la copia de seguridad.";
        }
        finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: No se pudo crear la copia de seguridad.";
            }
        }
        return nombreZip;
    }


    private boolean restaurarArchivos(String archivo)
    {
        File archivoZip=new File(getApplication().getFilesDir()+File.separator+"box"+File.separator+"backup",archivo);
        InputStream is;
        ZipInputStream zis;
        try
        {
            String archivoADescomprimir;
            is = new FileInputStream(archivoZip);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                archivoADescomprimir = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                /*if (ze.isDirectory()) {
                    File fmd = new File(path + archivoUnZip);
                    fmd.mkdirs();
                    continue;
                }*/

                // Comprobar si está descomprimiendo bbdd.
                if (!archivoADescomprimir.contains("database")) {
                    // Archivos de recursos
                    FileOutputStream fout = new FileOutputStream(getApplication().getFilesDir() + File.separator + "box" + File.separator + archivoADescomprimir);

                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();
                }else{
                    // Archivo de base de datos
                    FileOutputStream fout = new FileOutputStream(getApplication().getDataDir()+File.separator+archivoADescomprimir);

                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();
                }

            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Comprueba las copias de seguridad existentes y devuelve la última de ellas
     * @return String con el timestamp más reciente
     */
    public String checkBackups(){
        File [] files=getFilesFromDir("backup");
        if (files!=null && files.length>0) {
            String[] ts = new String[files.length];
            // Me quedo sólo con el timestamp de cada archivo
            for (int i = 0; i < files.length; i++) {
                ts[i] = files[i].getName().substring(3, files[i].getName().lastIndexOf("."));
            }
            // ordenar de mayor a menor
            long mayor = 0;
            long n = 0;
            for (int i = 0; i < ts.length; i++) {
                n = Long.parseLong(ts[i]);
                if (n > mayor) {
                    mayor = n;
                }
            }
            return "Última copia de seguridad: " + "bk_" + Long.toString(mayor) + ".zip\nCreada el " + utils.timestampToFecha(mayor);
        }
        return "Sin copias de seguridad.";
    }

    /**
     * Restaura copia de seguridad comprimida en Zip
     * @param archivo Archivo a restaurar
     */
    public boolean restaurarBackup(String archivo) {
        borrarTodosRecursos();
        restaurarArchivos(archivo);
        return true;

    }

}
