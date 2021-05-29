package com.agp.mybox.UI;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agp.mybox.Modelo.MyBoxRepository;
import com.agp.mybox.Modelo.POJO.OCR;
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
import java.util.Scanner;
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
    private final String RUTA_BOX=getApplication().getFilesDir()+File.separator+"box"+File.separator;
    private ExecutorService executor= Executors.newSingleThreadExecutor();
    private final MyBoxRepository mRepository;
    private MutableLiveData<String> errorTitulo=new MutableLiveData<>();
    private MutableLiveData<List<RecursoMini>> liveRecursosMini=new MutableLiveData<List<RecursoMini>>();
    private List<RecursoMini> recursosMinis = new ArrayList<RecursoMini>();
    private Utils utils=new Utils();
    private TessOCR mTessOCR=null;
    // "Trozos" (tramos de 250 caracteres para no pasar el max. permitido en VARCHAR de SQlite
    // para el OCR de Nuevo y Edición (Recuerdo)
    private ArrayList<String> trozosEdicion = new ArrayList<String>();
    private ArrayList<String> trozosNuevo = new ArrayList<String>();
    private boolean haciendoOCR=false;
    private MutableLiveData<Integer> liveOCR=new MutableLiveData<Integer>();
    private Boolean modoEdicion;
    private ArrayList<Recurso> listaRecursosBorrar=new ArrayList<Recurso>();

    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);


    // Constructor (se instancia el repositorio)
    public NuevoRecuerdoViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MyBoxRepository(application);
        liveOCR.setValue(6);
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
    public boolean guardarRecuerdo(String titulo, String comentarios, String etiquetas, String tiporecuerdo, boolean conOCR) {

        long fecha=utils.getTimestamp();

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
                        Long tamano=getFileSize(rm.getUri());
                        Recurso recurso=new Recurso(fecha,tamano,rm.getUri().toString(),idNuevoRecuerdo);
                        recursos.add(recurso);
                        mRepository.crearRecurso(recurso);
                        // Se comprueba que no se está haciendo OCR en este momento. Si es así, se deja el hilo que termine pero no se guardan los resultados
                        if (conOCR) {
                            // Comprobar que hay texto reconocido por el motor OCR
                            if (trozosNuevo.size() > 0) {
                                for (int i = 0; i < trozosNuevo.size(); i++) {
                                    OCR mOcr = new OCR(trozosNuevo.get(i), idNuevoRecuerdo, recurso.getUri());
                                    mRepository.crearOCR(mOcr);
                                }
                            }
                        }
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


     /**
     * Crear Recurso para el Recyclerview del Recuerdo.
     * Se discrimina si el parametro Recuerdo (objeto) es nulo o existe
     *
     * Nulo -> Modo Creación.
     * En este caso no se crean Recursos al no concerse el id del Recuerdo al que asociarlo ya que aún no existe en bd,
     * en su lugar se crea miniRecurso, que sólo requiere su Uri y una preview para el Recyclerview.
     *
     * Recuerdo existe -> Modo Edición
     * Se ha cargado para ver o editar un Recuerdo existente y en el Recyclerview se muestran los Recursos asociados al mismo.
     *
     * Se llama desde onActivityResult la recibir el callback del intent de la Cámara o selector de archivos
     * @param uri Uri (content:) del archivo seleccionado por el usuario (puede ser de cualquier tipo)
     * @param origen Para discriminar si el recurso a crear viene de la Cámara (valor = "foto") o es un archivo ya existen en el dispositivo (valor = "archivo")
     * @param recuerdo Recuerdo (objeto) que está cargado. Puede ser nulo por lo que indicaría que se está creando uno nuevo.
     */
    public void crearMiniRecursoLista(Uri uri,String origen, Recuerdo recuerdo){
        // Se activa modo edición si hay Recuerdo pasado desde la activity
        if (recuerdo==null){
            modoEdicion=false;
        }else {
            modoEdicion=true;
        }

        // Acción en función del tipo de recurso: foto (foto de la cámara) | archivo (archivo seleccionado)
        // Si es hay Recuerdo (modo edición) se crea el Recurso: copia en carpetas de aplicación y se guarda en BD lo que actualizará el RecyclerView (liveData).
        // Si es nulo, estamos en modo creación de nuevo Recuerdo y creamos miniRecurso (prescinde del id del Recuerdo como FK ya que aún no lo conocemos)
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
                if (recuerdo==null) { // modo creación. se crea miniRecurso
                    recursosMinis.add(new RecursoMini(mini, uri));
                    liveRecursosMini.setValue(recursosMinis); //Se actualiza el MutableLiveData para mostralo automáticamente en Recyclerview
                }else{ // Modo edicion (añadir Recurso a Recuerdo ya existente)
                    // Datos necesarios para crear el Recurso y guardarlo en base de datos
                    long fecha=utils.getTimestamp();
                    int recuerdoId=recuerdo.getId();
                    long size=getFileSize(uri);
                    // Se crea el recurso y se guarda en base de datos
                    Recurso recurso=new Recurso(fecha,size,uri.toString(),recuerdoId);
                    crearRecursoConIdBd(recurso);
                }

                // Hacer OCR si está activo
                if (mPrefs.getBoolean("OCR",false)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Modo edición
                            if (recuerdo!=null) {
                                String txt = hacerOCR(uri, "bitmap");
                                trocearTXT(txt, 250,true);
                                if (trozosEdicion.size() > 0) {
                                    // Recorrer el ArrayList ir crear el objeto OCR y registro en bd
                                    for (int i = 0; i < trozosEdicion.size(); i++) {
                                        OCR mOCR = new OCR(trozosEdicion.get(i), recuerdo.getId(),uri.toString());
                                        mRepository.crearOCR(mOCR);
                                    }
                                }
                            }else{
                                // Modo creación
                                String txt = hacerOCR(uri, "bitmap");
                                trocearTXT(txt, 250,false);
                            }
                        }
                    }).start();
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
                        String extension=getExtension(uri);
                        gestionarTipoTxt(uri, recuerdo, extension);
                        break;
                    default:
                        gestionarTipoOtros(uri, recuerdo);
                        break;
                }
                break;
        }

    }

    private void gestionarTipoTxt(Uri uriEntrada, Recuerdo recuerdo, String extension) {
        try {
            Uri uriFinal = copiarArchivo(uriEntrada,"otros", extension);
            // añadir a Lista miniRecursos para el RecyclerView.
            // Discriminar si es modo edición (Recurso) o creación (miniRecuerso)
            if (uriFinal!=null) {
                // evaluar si Recuerdo es nulo para crear miniRecuerdo (creación Recuerdo) o si no lo es, Recuerdo (edición)
                if (recuerdo == null) {
                    // Leer el contenido del archivo y "trocearlo" para la base de datos.
                    String contenido=leerArchivoTxt(uriFinal);
                    if (contenido!=null){
                        trocearTXT(contenido,255,false);
                        // la creacióon en bd se hará al guardar el Recuerdo.
                    }
                } else {
                    long fecha = utils.getTimestamp();
                    int id = recuerdo.getId();
                    long size = getFileSize(uriFinal);
                    Recurso recurso = new Recurso(fecha, size, uriFinal.toString(), id);
                    //mRepository.crearRecurso(r);
                    crearRecursoConIdBd(recurso);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),"No se puedo añadir el archivo",Toast.LENGTH_SHORT).show();
            Log.d("AGP","gestionarTipoTxt: Error al copiar el archivo a las carpetas de la aplicación");
        }
    }

    private void gestionarTipoImagen(Uri uriEntrada, String extension, Recuerdo recuerdo) {

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
                    Recurso recurso=new Recurso(fecha,size,uriFinal.toString(),id);
                    //mRepository.crearRecurso(r);
                    crearRecursoConIdBd(recurso);
                }

                // Comprobar si hay que hacer OCR o no
                if (mPrefs.getBoolean("OCR",false)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String txt = hacerOCR(uriEntrada, "bitmap");
                            if (recuerdo!=null) {
                                trocearTXT(txt, 250,true);
                                if (trozosEdicion.size() > 0) {
                                    // Recorrer el ArrayList ir crear el objeto OCR y registro en bd
                                    for (int i = 0; i < trozosEdicion.size(); i++) {
                                        OCR mOCR = new OCR(trozosEdicion.get(i), recuerdo.getId(),uriFinal.toString());
                                        mRepository.crearOCR(mOCR);
                                    }
                                }
                            }else{
                                trocearTXT(txt, 250,false);
                            }
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),"No se pudo agregar la imagen",Toast.LENGTH_LONG).show();
        }
    }


    private void gestionarTipoPdf(Uri uriEntrada, String extension, Recuerdo recuerdo){
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
                            Recurso recurso=new Recurso(fecha,size,uriFinal.toString(),id);
                            //mRepository.crearRecurso(r);
                            crearRecursoConIdBd(recurso);
                        }

                        // Comprobar si hay que hacer OCR o no
                        if (mPrefs.getBoolean("OCR",false)) {
                            String txt = hacerOCR(uriFinal,"pdf");
                            // Modo Edición: se trocea el texto y se guarda en bd ya que se conoce el id de Recuerdo
                            // Modo Creación: se trocea el texto y se guarda en el arraylista. El OCR se creará al guardar el Recuerdo y se conozca su id
                            if (recuerdo!=null) { // Mode edición
                                trocearTXT(txt, 250,true);
                                if (trozosEdicion.size() > 0) {
                                    // Recorrer el ArrayList y crear el objeto OCR y registro en bd
                                    for (int i = 0; i < trozosEdicion.size(); i++) {
                                        OCR mOCR = new OCR(trozosEdicion.get(i), recuerdo.getId(),uriFinal.toString());
                                        mRepository.crearOCR(mOCR);
                                    }
                                }
                            }else{
                                trocearTXT(txt, 250,false); // Modo nuevo Recuerdo
                            }
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
        // Copiar el archivo dado dentro de las carpetas de la aplicación (límite máx. de archivo indicado en LIMITE_MAX_ARCHIVO

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
                                Recurso recurso=new Recurso(fecha,size,uriFinal.toString(),id);
                                //mRepository.crearRecurso(r);
                                crearRecursoConIdBd(recurso);
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
        haciendoOCR=true;
        liveOCR.postValue(0);
        mTessOCR = new TessOCR(this.getApplication(),"spa");
        String txt=null;
        switch (tipo){
            case "bitmap":
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
                    Toast.makeText(getApplication(),"No se pudo hace OCR en la imagen", Toast.LENGTH_SHORT).show();
                }
                // Convert to ARGB_8888, required by tess
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                // Convertir a escala de grises para mejorar resultado OCR
                bitmap=convertirGris(bitmap);
                if (bitmap!=null) {
                    txt = mTessOCR.getBitmapOCR(bitmap);
                }
                break;
            case "pdf":
                // Crear archivo con el uri recibido
                // No está funcionando al venir de Download
                // String archivo=uri.toString().substring(uri.toString().lastIndexOf("/")+1,uri.toString().length());
                String archivo=archivoFromUri(uri);
                File f=new File(RUTA_BOX+"pdf/"+archivo);
                // Si el archivo se crea correctamente y se encuentra en la carpeta se procede al OCR
                if (f.exists()){
                    try {
                        // PdfRenderer para convertir la primera pág. del PDF a bitmap
                        ParcelFileDescriptor parcelFileDescriptor=ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY);
                        PdfRenderer pdfRenderer=new PdfRenderer(parcelFileDescriptor);
                        // Solo gestionaremos archivos de una pagina o la primera de ella para optimizar los recursos del dispositivo
                        // TODO: mejora para otras versiones gestionar más páginas
                        PdfRenderer.Page pagina=pdfRenderer.openPage(0);
                        // Ampliar el bitmap si es demasiado pequeño para mejorar OCR
                        int ancho=pagina.getWidth();
                        int alto=pagina.getHeight();
                        if (ancho<1000){
                            int dif=1500-ancho;
                            float relacion=1500/ancho;
                            ancho=(int)Math.round(ancho*relacion);
                            alto=(int)Math.round(alto*relacion);
                        }
                        /*if (ancho<=800 || alto<=800){
                            ancho=(int)Math.round((ancho*2.5));
                            alto=(int)Math.round((alto*2.5));
                        }*/
                        Bitmap bitmapPagina=Bitmap.createBitmap(ancho, alto,Bitmap.Config.ARGB_8888);
                        //poner fondo blanco para mejorar OCR
                        Canvas canvas = new Canvas(bitmapPagina);
                        canvas.drawColor(0xFFFFFFFF);
                        pagina.render(bitmapPagina,null,null,PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                        bitmapPagina=convertirGris(bitmapPagina);
                        txt=mTessOCR.getBitmapOCR(bitmapPagina);
                        //pdfRenderer.close();
                    }catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(getApplication(),"No se ha podido realizar OCR en el PDF",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        Log.d("AGP_OCR", "hacerOCR: Finalizado ok");
        haciendoOCR=false;
        liveOCR.postValue(8);
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

    public void trocearTXT(String txt, int tamano, boolean modoEdicion){
        // Modo Edición: se borra lo que hibiera en el array y trocea el texto. se crea el registro en bd de datos al devolver el resultado
        if (modoEdicion){
            trozosEdicion.clear();
            int contador=0;
            while (contador<txt.length()){
                trozosEdicion.add(txt.substring(contador,Math.min(contador+tamano,txt.length())));
                contador+=tamano;
            }
        }
        // Modo Nuevo Recuerdo: se va almacenando los trozos de todos los posible Recursos y el OCR se creará en el último  momento al guardar el Recuerdo
        else {
            int contador=0;
            while (contador<txt.length()){
                trozosNuevo.add(txt.substring(contador,Math.min(contador+tamano,txt.length())));
                contador+=tamano;
            }
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

    public void borrarMiniRecursos(List<RecursoMini> recursoMini) {
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

    public void borrarRecursos(){
        // Comprobar si existen Recursos creados al editar el Recuerdo. Si no es así no hay que borrar nada
        if (listaRecursosBorrar.size()>0){
            for (Recurso r:listaRecursosBorrar){
                // Borrar de disco
                Uri uri=Uri.parse(r.getUri());
                // Borrar el archivo del almacenamiento de la aplicación
                try{
                    getApplication().getContentResolver().delete(uri,null,null);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplication(),"No se ha podido borrar el archivo del almacenamiento.",Toast.LENGTH_SHORT).show();
                }

                // Borrar en bd
                mRepository.borrarRecurso(r);

                // Borrar OCR si lo tiene
                try {
                    mRepository.borrarOCRdeUri(r.getUri());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("AGP","borrarRecursos: Error al borrar los registros OCR asociados");
                }
            }
        }

            /*
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    for (Recurso r:listaRecursosBorrar){
                        // Borrar en bd
                        mRepository.borrarRecurso(r);
                        // Borrar de disco
                        String uri=r.getUri();
                        File f=new File(uri);
                        if (f.exists() && !f.isDirectory()){
                            f.delete();
                        }
                    }
                }
            };
            executor.submit(runnable);
            */
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
        String extension;

        // Comprobar esquema del uri
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            // Esquema "content"
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getApplication().getContentResolver().getType(uri));
        } else {
            // Otro esquema
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;

        //FUNCIONANDO DE FORMA BASICA
        /*
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

         */
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

    private void crearRecursoConIdBd(Recurso recurso){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRepository.crearRecurso(recurso);
                recurso.setId(mRepository.getLastIdRecurso());
                // Lista de Recursos que se van creando (en disco y bd). Se usa para eliminarlos si se cancela la edición.
                listaRecursosBorrar.add(recurso);
            }
        }).start();
    }

    private String archivoFromUri(Uri uri){
        String archivo=null;

        // Comprobar esquema del uri
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            // Esquema "content"
            String ruta=uri.getPath().toString();
            archivo=ruta.substring(ruta.lastIndexOf("/")+1,ruta.length());

        } else {
            // Otro esquema
            File f=new File(uri.getPath());
            archivo=f.getName();

        }
        return archivo;
    }

    public LiveData<Integer> estadoOCR(){
        return liveOCR;
    }

    public boolean getHaciendoOcr(){
        return haciendoOCR;
    }

    public String leerArchivoTxt(Uri uri){
        String contenido=null;
        String archivo=uri.toString().substring(uri.toString().lastIndexOf("/")+1,uri.toString().length());
        File file=new File(RUTA_BOX+"otros"+File.separator+archivo);
        if (file.exists() && !file.isDirectory()){
            try {
                Scanner entrada=new Scanner(file);
                while (entrada.hasNextLine()){
                    String linea=entrada.nextLine();
                    contenido+=linea;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return contenido;
    }
}
