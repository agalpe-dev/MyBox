package com.agp.mybox.UI;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.Compuestos.RecursoMini;
import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.R;
import com.agp.mybox.Utils.TessOCR;
import com.agp.mybox.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Antonio Gálvez on 04/04/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class NuevoRecuerdoViewModel extends AndroidViewModel {
    private final long LIMITE_MAX_ARCHIVO=5120000;
    private ExecutorService executor= Executors.newSingleThreadExecutor();
    private final MyBoxRepository mRepository;
    private MutableLiveData<String> errorTitulo=new MutableLiveData<>();
    private MutableLiveData<List<RecursoMini>> liveRecursosMini=new MutableLiveData<List<RecursoMini>>();
    private List<RecursoMini> recursosMinis = new ArrayList<RecursoMini>();
    private Utils utils=new Utils();
    private TessOCR mTessOCR=null;
    private ArrayList<String> trozos=new ArrayList<String>();

    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);


    // Constructor (se instancia el repositorio)
    public NuevoRecuerdoViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
    }

    /**
     * Método que crea nuevo Recuerdo y lo guarda en base de datos.
     * Se hace una comprobación del título y mediante un observable se comunica con UI en el caso
     * que esté vacío, lo que cancela la creación del Recuerdo y su almacenamiento en BD
     * Se pasan los datos necesarios para crear el Recuerdo desde la intefaz
     * @param titulo
     * @param comentarios
     * @param etiquetas
     * @param tiporecuerdo
     * @return
     */
    public boolean guardarRecuerdo(String titulo, String comentarios, String etiquetas, String tiporecuerdo) {
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

        // Comprobar que el título no está vacío.
        if(checkTitulo(titulo)){
            int idTipoRecuerdo=mRepository.getTipoRecuerdoID(tiporecuerdo);

            // Creación del nuevo Recuerdo (objeto)
            Recuerdo recuerdo = new Recuerdo(titulo, fecha, comentarios, 0, idTipoRecuerdo);

            // Guardar el nuevo Recuerdo en base de datos
            try {
                mRepository.crearRecuerdo(recuerdo);

                // Crear ArrayList de los recursos asociados al recuerdo si existen.
                // Se obtienen los minirecursos de la lista y se crean
                // los recursos equivalentes una vez conocido el id del Recuerdo nuevo
                // Para obtener id del recuerdo recien creado se hace una consulta a la bbdd para busca el id
                // del ultimo recuerdo guardado (por el fecha, que es el timestamp creado al crear el Recuerdo)
                if (recursosMinis.size()>0){
                    int idNuevoRecuerdo=0;
                    do{
                        idNuevoRecuerdo = mRepository.getIdRecuerdoPorFecha(fecha);
                    } while (idNuevoRecuerdo==0);


                    // Lista de Recursos a crear
                    ArrayList<Recurso> recursos=new ArrayList<>();
                    for (RecursoMini rm: recursosMinis){
                        //AssetFileDescriptor fileDescriptor=getApplication().getApplicationContext().getContentResolver().openAssetFileDescriptor(rm.getUri(),"r");
                        //long tamano=fileDescriptor.getLength();
                        Long tamano=getFileSize(rm.getUri());
                        Recurso recurso=new Recurso(fecha,tamano,rm.getUri().toString(),idNuevoRecuerdo);
                        recursos.add(recurso);
                        mRepository.crearRecurso(recurso);

                    }
                }

                Toast.makeText(getApplication(), R.string.recuerdoGuardado, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), R.string.errorGuardarRecuerdo, Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return false;
        }
    }

    // Comprobar si el titulo dado al recuerdo está vacío (no se permite guardar sin un título)
    // Se actualiza el livedata para mostrar aviso en la interfaz
    private boolean checkTitulo(String titulo){
        if (titulo.isEmpty()) {
            errorTitulo.setValue(String.valueOf(R.string.avisoTituloVacio));
            return false;
        }
        else {
            errorTitulo.setValue("");
            return true;
        }
    }

    /**
     * LiveData que devuelve el texto de error en relación al título dado al recuerdo (no puede estar en vacío)
     * @return String con aviso de error
     */
    public LiveData<String> getErrorTitulo(){
        return errorTitulo;
    }


    // Crear achivo de imagen para guardar la foto tomada con la cámara
    public File crearArchivoImagen() throws IOException{
        String formato = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
        String nombreArchivo="IMG_"+formato;
        File dir=new File(getApplication().getFilesDir()+"/box/camara");
        File archivo=File.createTempFile(nombreArchivo,".jpg",dir);
        return archivo;
    }


    // Crear Recurso para el Recyclerview del Recuerdo
    // Se discrimina si el parametro Recuerdo es nulo (modo creación Recuerdo, se trabaja con minirecursos)
    // o no es nulo (modo edición, se cargan los datos de los Recursos asociados al Recuerdo)
    // Se llama desde onActivityResult la recibir el callback del intent de la Cámara o selector de archivos
    public void crearMiniRecursoLista(Uri uri,String origen, Recuerdo recuerdo){
        // Acción en función del tipo de recurso: foto (foto de la cámara) | archivo (archivo seleccionado)
        // Se recibe Recuerdo para discriminar si estamos en modo edición.
        // Si es hay Recuerdo se crea Recurso y se guarda en BD lo que actualizará el RecyclerView.
        // Si es nulo, estamos en modo creación de Recuerdo y creamos miniRecurso (prescinde del id del Recuerdo como FK ya que aún no lo conocemos)
        switch (origen){
            // foto creada con la cámara. Se carga en bitmap y se crea thumbnail
            case "foto":
                Bitmap bitmap= null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap mini= ThumbnailUtils.extractThumbnail(bitmap,80,80);
                // Comprobar si estamos modo edición o creación
                if (recuerdo==null) { // modo creación
                    recursosMinis.add(new RecursoMini(mini, uri));
                    liveRecursosMini.setValue(recursosMinis);
                }else{ // Modo edicion (añadir recurso a Recuerdo ya existente)
                    long fecha=utils.getTimestamp();
                    int id=recuerdo.getId();
                    long size=getFileSize(uri);
                    Recurso r=new Recurso(fecha,size,uri.toString(),id);
                    mRepository.crearRecurso(r);
                }
                break;

           // Archivo seleccionado del equipo
            case "archivo":
                String tipo=getTipoArchivo(uri);
                switch (tipo){
                    case "image/jpeg":
                        gestionarTipoImagen(uri, "jpg",recuerdo);
                        break;
                    case "image/gif":
                        gestionarTipoImagen(uri, "gif", recuerdo);
                        break;
                    case "image/tiff":
                        gestionarTipoImagen(uri, "tif",recuerdo);
                        break;
                    case "image/bmp":
                        gestionarTipoImagen(uri,"bmp",recuerdo);
                        break;
                    case "image/png":
                        gestionarTipoImagen(uri, "png",recuerdo);
                        break;
                    case "application/pdf":
                        gestionarTipoPdf(uri, "pdf", recuerdo);
                        break;
                    case "text/plain":
                        //gestionarTipoTxt(uri);
                        break;
                    default:
                        gestionarTipoOtros(uri, recuerdo);
                        break;
                }
                break;
        }

    }

    private void gestionarTipoImagen(Uri uriEntrada, String extension, Recuerdo recuerdo) {
        // Comprobar si hay que hacer OCR o no
        if (mPrefs.getBoolean("OCR",false)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String txt = hacerOCR(uriEntrada, "bitmap");
                    trocearTXT(txt,255);
                    Log.d("AGP_OCR", "Hilo: " + txt);
                    Log.d("AGP_OCR","Trozos: " + Integer.toString(trozos.size()));
                }
            }).start();

        }

        try {
            Uri uriFinal = copiarArchivo(uriEntrada,"imagen", extension);
            // añadir a Lista miniRecursos para el RecyclerView.
            // Discriminar si es modo edición (Recurso) o creación (miniRecuerso)
            if (uriFinal!=null) {
                // evaluar si Recuerdo es nulo para crear miniRecuerdo (creación Recuerdo) o si no lo es, Recuerdo (edición)
                if (recuerdo==null) {
                    // Obtener thumbnail de la imagen (en el caso de archivos no imagen se creará genérico)
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uriFinal);
                    Bitmap mini = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
                    recursosMinis.add(new RecursoMini(mini, uriFinal));
                    liveRecursosMini.setValue(recursosMinis);
                }else{
                    long fecha=utils.getTimestamp();
                    int id=recuerdo.getId();
                    long size=getFileSize(uriFinal);
                    Recurso r=new Recurso(fecha,size,uriFinal.toString(),id);
                    mRepository.crearRecurso(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),"No se pudo agregar la imagen",Toast.LENGTH_LONG).show();
        }
    }


    private void gestionarTipoPdf(Uri uriEntrada, String extension, Recuerdo recuerdo){
        // Comprobar si hay que hacer OCR o no
        if (mPrefs.getBoolean("OCR",false)) {
            //String txt = hacerOCR(uriEntrada); // implementar en hilo independiente
        }

        // copiar el archivo en las carpetas del sistema
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uriFinal = copiarArchivo(uriEntrada,"pdf", extension);
                    // añadir a Lista miniRecursos para el RecyclerView.
                    // Discriminar si es modo edición (Recurso) o creación (miniRecuerso)
                    if (uriFinal!=null) {
                        // evaluar si Recuerdo es nulo para crear miniRecuerdo (creación Recuerdo) o si no lo es, Recuerdo (edición)
                        if (recuerdo==null) {
                            // Crear thumbnail del drawable "tipo_pdf"
                            //Bitmap bitmap= BitmapFactory.decodeResource(getApplication().getResources(),R.drawable.tipo_pdf);
                            Bitmap bitmap= drawableToBitmap(R.drawable.tipo_pdf);
                            Bitmap mini = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
                            recursosMinis.add(new RecursoMini(mini, uriFinal));
                            //liveRecursosMini.setValue(recursosMinis);
                            liveRecursosMini.postValue(recursosMinis);
                        }else{
                            long fecha=utils.getTimestamp();
                            int id=recuerdo.getId();
                            long size=getFileSize(uriFinal);
                            Recurso r=new Recurso(fecha,size,uriFinal.toString(),id);
                            mRepository.crearRecurso(r);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplication(),"No se pudo agregar la imagen",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void gestionarTipoOtros(Uri uriEntrada, Recuerdo recuerdo){
        String extension=getExtension(uriEntrada);
        // Copiar el archivo dado dentro de las carpetas de la aplicación (límite máx. de archivo
        // indicado en constante

        if (getFileSize(uriEntrada)<=LIMITE_MAX_ARCHIVO){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Uri uriFinal = copiarArchivo(uriEntrada,"otros", extension);
                        // añadir a Lista miniRecursos para el RecyclerView.
                        // Discriminar si es modo edición (Recurso) o creación (miniRecuerso)
                        if (uriFinal!=null) {
                            // evaluar si Recuerdo es nulo para crear miniRecuerdo (creación Recuerdo) o si no lo es, Recuerdo (edición)
                            if (recuerdo==null) {
                                // Crear bitmap con texto
                                Bitmap bitmap = textoABitmap(extension,14);
                                Bitmap mini = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
                                recursosMinis.add(new RecursoMini(bitmap, uriFinal));
                                liveRecursosMini.postValue(recursosMinis);
                            }else{
                                long fecha=utils.getTimestamp();
                                int id=recuerdo.getId();
                                long size=getFileSize(uriFinal);
                                Recurso r=new Recurso(fecha,size,uriFinal.toString(),id);
                                mRepository.crearRecurso(r);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplication(),"No se pudo agregar el archivo",Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
        else {
            // Aviso no se puede copiar el archivo por superar el tamaño
            Toast.makeText(getApplication(),"El archivo es demasiado grande.\nSeleccione máximo 5 MB.",Toast.LENGTH_LONG).show();
        }

    }

    private Uri copiarArchivo(Uri uriEntrada, String carpeta, String extension) throws IOException{
        String uuid = UUID.randomUUID().toString(); // nombre de archivo
        String archivoSalida=getApplication().getFilesDir()+"/box/"+carpeta+"/"+uuid+"."+extension;
        File file=new File(archivoSalida);
        Uri uriSalida= FileProvider.getUriForFile(getApplication(),"com.agp.mybox.fileprovider",file);
        InputStream inputStream=getApplication().getContentResolver().openInputStream(uriEntrada);
        OutputStream outputStream=getApplication().getContentResolver().openOutputStream(uriSalida);
        if (inputStream==null || outputStream==null){
            return null;
        }
        // Copia de los datos archivo fuente a archivo final
        byte[] buffer=new byte[1024];
        int tramo;
        while ((tramo = inputStream.read(buffer))>0){
            outputStream.write(buffer,0,tramo);
        }
        inputStream.close();
        outputStream.close();
        return uriSalida;
    }

    private String hacerOCR(Uri uri, String tipo) {
        mTessOCR = new TessOCR(this.getApplication(),"spa");
        String txt=null;
        if (tipo=="bitmap"){
            Bitmap bitmap= null;
            try {
                //bitmap = BitmapFactory.decodeFile(MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),uri));
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
                ExifInterface exif=new ExifInterface(getApplication().getContentResolver().openInputStream(uri));
                int exifOrientacion=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                int rotar=0;
                switch (exifOrientacion) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotar = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotar = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotar = 270;
                        break;
                }

                if (rotar!=0){
                    bitmap = utils.rotarImagen(bitmap,rotar);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            // Convertir a escala de grises para mejorar resultado OCR
            bitmap=convertirGris(bitmap);
            if (bitmap!=null) {
                txt = mTessOCR.getBitmapOCR(bitmap);
            }
        }
        Log.d("AGP_OCR", "hacerOCR: "+txt);
        return txt;

    }

    public Bitmap convertirGris(Bitmap bitmap) {
        // https://stackoverflow.com/questions/3373860/convert-a-bitmap-to-grayscale-in-android
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;
    }

    private void trocearTXT(String txt, int tamano){
        int contador=0;
        while (contador<=txt.length()){
            trozos.add(txt.substring(0,tamano-1));
            contador+=tamano-1;
        }
    }

    public String getTipoArchivo(Uri uri){
        String tipo=getApplication().getContentResolver().getType(uri);
        return tipo;
    }

    public LiveData<List<Recurso>> RecursosDeRecuerdo(int idRecuerdo){
        return mRepository.RecursosDeRecuerdo(idRecuerdo);
    }


    public LiveData<List<RecursoMini>> getRecursosMini(){
        return liveRecursosMini;
    }

    public boolean actualizarRecuerdo(String titulo, String comentarios, String etiquetas, String tiporecuerdo, int idRecuerdo) {
        if(checkTitulo(titulo)){
            long fecha=utils.getTimestamp();
            int i=mRepository.getTipoRecuerdoID(tiporecuerdo);

            Recuerdo recuerdo = new Recuerdo(titulo, fecha, comentarios, 0, i);
            recuerdo.setId(idRecuerdo);

            try {
                mRepository.actualizarRecuerdo(recuerdo);
                Toast.makeText(getApplication(), R.string.recuerdoActualizado, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), R.string.errorActualizarRecuerdo, Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return false;
        }
    }

    public void borrarRecursos(List<RecursoMini> recursoMini) {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                for (RecursoMini rm : recursoMini){
                    try {
                        getApplication().getContentResolver().delete(rm.getUri(),null,null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        executor.submit(runnable);
    }

    public String getTipoRecuerdoPorId(int id){
        return mRepository.getTipoRecuerdoPorId(id);
    }

    // Obtener tamaño del archivo según su URI
    public long getFileSize(Uri uri){
        AssetFileDescriptor fileDescriptor= null;
        try {
            fileDescriptor = getApplication().getContentResolver().openAssetFileDescriptor(uri,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long size=fileDescriptor.getLength();
        return size;
    }

    // Obtener extensión de URI
    private String getExtension(Uri uri){
        String extension=null;
        String tipo=getTipoArchivo(uri);
        String[] s=tipo.split("/");
        switch (s[1]){
            case "msword":
                extension="doc";
                break;
            default:
                extension=s[1];
                break;
        }
        return extension;
    }

    // Crear imagen con texto de extensión
    // https://stackoverflow.com/questions/8799290/convert-string-text-to-bitmap
    public Bitmap textoABitmap(String texto, float textoSize) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textoSize);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(texto) + 0.0f);
        int height = (int) (baseline + paint.descent() + 0.0f);

        int trueWidth = width;
        if(width>height)height=width; else width=height;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(texto, width/2-trueWidth/2, baseline, paint);
        return utils.rotarImagen(image,-90);
    }


    private Bitmap drawableToBitmap(int drawableId){
        Drawable drawable= ContextCompat.getDrawable(getApplication(),drawableId);
        if(drawable!=null){
            Bitmap b=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas c=new Canvas(b);
            drawable.setBounds(0,0,c.getWidth(),c.getHeight());
            drawable.draw(c);
            //c.rotate(180,c.getWidth()/2,c.getHeight()/2);
            b = utils.rotarImagen(b,-90);
            return b;
        }
        return null;
    }

}
