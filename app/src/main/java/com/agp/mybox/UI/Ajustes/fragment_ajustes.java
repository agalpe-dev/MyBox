package com.agp.mybox.UI.Ajustes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.mybox.R;

public class fragment_ajustes extends Fragment {

    private FragmentAjustesViewModel mViewModel;
    private Switch swAvisos, swOCR;
    private TextView mNCamara, mNImagen, mNPdf, mNOtros, mTamCamara, mTamImagen, mTamPdf, mTamOtros;
    private ImageButton mBtBorrarCamara, mBtBorrarImagen, mBtBorrarPdf, mBtBorrarOtros;
    private Button mBtBorrarTodo;

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

        mBtBorrarCamara=v.findViewById(R.id.btBorrarCamara);
        mBtBorrarImagen =v.findViewById(R.id.btBorrarImagen);
        mBtBorrarPdf=v.findViewById(R.id.btBorrarPdf);
        mBtBorrarOtros=v.findViewById(R.id.btBorrarOtros);
        mBtBorrarTodo=v.findViewById(R.id.btBorrarTodo);

        // Listeners de botones para borrar
        mBtBorrarCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAviso("camara");
            }
        });

        mBtBorrarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAviso("imagen");
            }
        });

        mBtBorrarPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAviso("pdf");
            }
        });

        mBtBorrarOtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAviso("otros");
            }
        });

        mBtBorrarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearAviso("todo");
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


        // Datos de archivos y tamaños por carpetas
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


    private void crearAviso(String carpeta){
        AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.borrarArchivos);
        dialog.setMessage(R.string.avisoBorrarRecursos);
        dialog.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (carpeta!=null) {
                    mViewModel.borrarRecursos(carpeta);
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


}