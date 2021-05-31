package com.agp.mybox.UI.Favoritos;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import com.agp.mybox.UI.NuevoRecuerdoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class fragment_favoritos extends Fragment implements recuerdoAdapter.ItemClickListener{

    FloatingActionButton mFAB;
    private FragmentFavoritosViewModel mViewModel;
    private RecyclerView mRv;
    private recuerdoAdapter adapter=new recuerdoAdapter(this,getActivity());

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

        // Acceso a FAB que carga Activity para crear nuevo Recuerdo
        mFAB=(FloatingActionButton) v.findViewById(R.id.boton_crear_fav);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigation.findNavController(v).navigate(R.id.fragment_nuevoRecuerdo);
                startActivity(new Intent(getActivity(), NuevoRecuerdoActivity.class));
            }
        });

        // Observar el livedata que proporciona el viewmodel
        mViewModel.getRecuerdosFavoritos().observe(getActivity(), new Observer<List<Recuerdo>>() {
            @Override
            public void onChanged(List<Recuerdo> recuerdos) {
                adapter.setmListaRecuerdos(recuerdos);
            }
        });

        // Observar del livedata que comparte el adapter del Recyclerview con el id del
        // Recuerdo al que se ha pulsado en borón de favorio
        adapter.getFavoritoOn().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mViewModel.favoritoON(integer);
            }
        });

        adapter.getFavoritoOff().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mViewModel.favoritoOFF(integer);
            }
        });


        // Devolución de la vista
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onItemClick(Recuerdo recuerdo) {

    }
}