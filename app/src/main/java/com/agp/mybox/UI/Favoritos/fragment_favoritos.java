package com.agp.mybox.UI.Favoritos;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agp.mybox.Adaptadores.recuerdoAdapter;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.R;

import java.util.List;

public class fragment_favoritos extends Fragment {

    private FragmentFavoritosViewModel mViewModel;
    private RecyclerView mRv;
    private recuerdoAdapter adapter=new recuerdoAdapter();

    public static fragment_favoritos newInstance() {
        return new fragment_favoritos();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_favoritos, container, false);
        View v=inflater.inflate(R.layout.fragment_favoritos, container, false);

        //Instanciación del viewModel
        mViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentFavoritosViewModel.class);

        //Preparación de RecyclerView
        mRv=(RecyclerView)v.findViewById(R.id.rvFavoritos);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(llm);
        mRv.setHasFixedSize(true);
        mRv.setAdapter(adapter);

        //observar el livedata que proporciona el viewmodel
        mViewModel.getRecuerdosFavoritos().observe(getActivity(), new Observer<List<Recuerdo>>() {
            @Override
            public void onChanged(List<Recuerdo> recuerdos) {
                adapter.setRecuerdos(recuerdos);
            }
        });


        //devolución de la vista
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}