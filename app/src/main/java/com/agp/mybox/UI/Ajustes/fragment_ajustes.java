package com.agp.mybox.UI.Ajustes;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.agp.mybox.R;
import com.agp.mybox.UI.Inicio.FragmentInicioViewModel;

import java.util.Observer;

public class fragment_ajustes extends Fragment {

    private FragmentAjustesViewModel mViewModel;
    private Switch swAvisos, swOCR;

    public static fragment_ajustes newInstance() {
        return new fragment_ajustes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Acceso a la vista
        final View v=inflater.inflate(R.layout.fragment_ajustes,container,false);
        mViewModel= new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentAjustesViewModel.class);

        swAvisos= v.findViewById(R.id.botonAvisos);
        swOCR= v.findViewById(R.id.botonOCR);

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

        // Se devuelve la vista
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



}