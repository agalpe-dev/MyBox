package com.agp.mybox.UI.Ajustes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.mybox.R;
import com.agp.mybox.UI.Backups;
import com.agp.mybox.Utils.Utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.provider.DocumentsContract.EXTRA_INITIAL_URI;

public class fragment_ajustes extends Fragment {

    private final int PEDIR_ARCHIVO=1;
    private final String AUTORIDAD="com.agp.mybox.fileprovider";
    private Utils utils=new Utils();
    private Handler handler=new Handler();
    private FragmentAjustesViewModel mViewModel;
    private Switch swAvisos, swOCR;
    private TextView mNCamara, mNImagen, mNPdf, mNOtros, mTamCamara, mTamImagen, mTamPdf, mTamOtros, mAvisoBackup;
    private ImageButton mBtBorrarCamara, mBtBorrarImagen, mBtBorrarPdf, mBtBorrarOtros;
    private Button mBtBorrarTodo, mBotonCrearBackup, mBotonRestaurarBackup;
    private ProgressBar mProgressBar;

    public static fragment_ajustes newInstance() {
        return new fragment_ajustes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Acceso a la vista
        final View v=inflater.inflate(R.layout.fragment_ajustes,container,false);
        mViewModel= new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentAjustesViewModel.class);

        // Setear controles
        swAvisos= v.findViewById(R.id.botonAvisos);
        swOCR= v.findViewById(R.id.botonOCR);
        mNCamara=v.findViewById(R.id.txtNCamara);
        mNImagen =v.findViewById(R.id.txtNImagen);
        mNPdf=v.findViewById(R.id.txtNPdf);
        mNOtros=v.findViewById(R.id.txtNOtros);
        mTamCamara=v.findViewById(R.id.txtTamCamara);
        mTamImagen =v.findViewById(R.id.txtTamImagen);
        mTamPdf=v.findViewById(R.id.txtTamPdf);
        mTamOtros=v.findViewById(R.id.txtTamOtros);
        mAvisoBackup=v.findViewById(R.id.avisoBackup);

        mBtBorrarCamara=v.findViewById(R.id.btBorrarCamara);
        mBtBorrarImagen =v.findViewById(R.id.btBorrarImagen);
        mBtBorrarPdf=v.findViewById(R.id.btBorrarPdf);
        mBtBorrarOtros=v.findViewById(R.id.btBorrarOtros);
        mBtBorrarTodo=v.findViewById(R.id.btBorrarTodo);
        mBotonCrearBackup=v.findViewById(R.id.botonCrearBackup);
        mBotonRestaurarBackup=v.findViewById(R.id.botonRestaurarBackup);

        mProgressBar=v.findViewById(R.id.barraProgreso);

        // Al cargar el fragment se comprueba el último archivo de backup creado
        mAvisoBackup.setText(mViewModel.checkBackups());

        // Listeners de botones para borrar
        mBtBorrarCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAvisoBorrar("camara");
            }
        });

        mBtBorrarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAvisoBorrar("imagen");
            }
        });

        mBtBorrarPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAvisoBorrar("pdf");
            }
        });

        mBtBorrarOtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAvisoBorrar("otros");
            }
        });

        mBtBorrarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAvisoBorrar("todo");
            }
        });

        // Acción botón Restaurar backup
        mBotonRestaurarBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobar si existe algún archivo de backup. Si no existe, no se carga la activity y se muestra aviso
                String hayBackup=mViewModel.checkBackups();
                if (hayBackup.contains(".zip")) {
                    Intent intent = new Intent(getActivity(), Backups.class);
                    File[] files = mViewModel.getFilesFromDir("backup");
                    ArrayList<String> listaArchivos = new ArrayList<>();
                    for (File f : files) {
                        listaArchivos.add(f.getName());
                    }
                    intent.putExtra("LISTA_ARCHIVOS", listaArchivos);
                    startActivityForResult(intent, PEDIR_ARCHIVO);
                }else{
                    // No exite ningún archivo de backup
                    Toast.makeText(getActivity(),"No existen copias de seguridad.",Toast.LENGTH_LONG).show();
                }


            }
        });

        // Botón CrearBackup
        mBotonCrearBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Activar ProgressBar y cambiar texto del botón
                mProgressBar.setVisibility(View.VISIBLE);
                mBotonCrearBackup.setEnabled(false);
                mBotonCrearBackup.setText(R.string.creando);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // backupArchivos devuelve string con el nombre del archivo creado
                        String resultado=mViewModel.backupArchivos();
                        String ts=resultado.substring(3,resultado.lastIndexOf("."));
                        String fecha=utils.timestampToFecha(Long.parseLong(ts));
                        //Log.d("BACKUP",resultado + " - "+ ts + " - " + fecha);
                        if (resultado.contains("zip")){
                            // Se ejecuta en hilo ppal para poder acceder al interfaz de usuario
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAvisoBackup.setText("Última copia de seguridad: "+resultado+".\nCreada el "+fecha);
                                    mBotonCrearBackup.setEnabled(true);
                                    mBotonCrearBackup.setText(R.string.btHacerBackup);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAvisoBackup.setText(resultado);
                                    mBotonCrearBackup.setEnabled(true);
                                    mBotonCrearBackup.setText(R.string.btHacerBackup);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }


                    }
                }).start();
            }
        });


        // Establecer valores de los switchbutton según lo almacenado en SharedPreferences
        swAvisos.setChecked(mViewModel.checkPreferencias("Avisos"));
        swOCR.setChecked(mViewModel.checkPreferencias("OCR"));

        // Listeners para SwitchButtons
        swAvisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setPreferencias("Avisos",swAvisos.isChecked());
            }
        });
        swOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setPreferencias("OCR",swOCR.isChecked());
            }
        });


        // Datos de archivos y tamaños por carpetas - Textviews
        mNCamara.setText(mViewModel.getNArchivos("camara"));
        mNImagen.setText(mViewModel.getNArchivos("imagen"));
        mNPdf.setText(mViewModel.getNArchivos("pdf"));
        mNOtros.setText(mViewModel.getNArchivos("otros"));

        mTamCamara.setText(mViewModel.getFilesSize("camara"));
        mTamImagen.setText(mViewModel.getFilesSize("imagen"));
        mTamPdf.setText(mViewModel.getFilesSize("pdf"));
        mTamOtros.setText(mViewModel.getFilesSize("otros"));


        // Se devuelve la vista
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // Gestionar el nombre del archivo backup seleccionado para restaurar y devuelvo por la activity correspondiente
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==PEDIR_ARCHIVO){
            if (data.getExtras().containsKey("ARCHIVO")){
                String archivo=data.getExtras().getString("ARCHIVO");
                crearAviso("Restaurar una copia de seguridad implica eliminar todos los datos actuales de la aplicación.\n\n¿Estás seguro?",archivo);
            }
        }
    }

    private void crearAvisoBorrar(String carpeta){
        AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.borrarArchivos);
        dialog.setMessage(R.string.avisoBorrarRecursos);
        dialog.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (carpeta!=null) {
                    mViewModel.borrarRecursos(carpeta,true);
                    // Toast.makeText(getActivity(), "Archivos eliminados", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Hay un problema y no se pueden eliminar los archivos en este momento.\n Vuelve a intentarlo.", Toast.LENGTH_LONG).show();
                }
            }
        })
                .setNegativeButton(R.string.boton_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).show();

    }

    private void crearAviso(String txt, String archivo){
        AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.aviso);
        dialog.setMessage(txt);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restaurarBackup(archivo);
            }
        })
                .setNegativeButton(R.string.boton_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void restaurarBackup(String archivo){
        //TODO Eliminar archivos backups
        mProgressBar.setVisibility(View.VISIBLE);
        mBotonRestaurarBackup.setText(R.string.restaurando);
        mBotonRestaurarBackup.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msj="";
                if (mViewModel.restaurarBackup(archivo)){
                    msj="Copia de seguridad restaurada correctamente.";
                }else{
                    msj="Error. No se pudo restaurar la copia de seguridad";
                }
                String finalMsj = msj;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), finalMsj,Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                        mBotonRestaurarBackup.setText(R.string.restaurarBackup);
                        mBotonRestaurarBackup.setEnabled(true);
                    }
                });
            }
        }).start();

    }
}