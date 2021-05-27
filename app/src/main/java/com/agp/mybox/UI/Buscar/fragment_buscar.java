package com.agp.mybox.UI.Buscar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.agp.mybox.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_buscar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_buscar extends Fragment {

    private FragmentBuscarViewModel mViewModel;
    private SearchView mCajaBusqueda;

    public fragment_buscar() {
        // Required empty public constructor
    }

    public static fragment_buscar newInstance() {
        fragment_buscar fragment = new fragment_buscar();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_buscar, container, false);

        mViewModel=new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentBuscarViewModel.class);

        mCajaBusqueda=v.findViewById(R.id.cajaBusqueda);

        mCajaBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
                //TODO buscar en comentarios, OCR, etiquetas y añadir a List y este ViewModel para
                //usarlo como Livedata para el adaptador del Recyclerview.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return v;
    }
}