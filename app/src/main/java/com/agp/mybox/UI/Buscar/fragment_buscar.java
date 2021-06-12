package com.agp.mybox.UI.Buscar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.agp.mybox.Adaptadores.recuerdoAdapter;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.R;
import com.agp.mybox.UI.NuevoRecuerdoActivity;

import java.util.List;


public class fragment_buscar extends Fragment implements recuerdoAdapter.ItemClickListener {

    private FragmentBuscarViewModel mViewModel;
    private SearchView mCajaBusqueda;
    private RecyclerView mRv;
    private recuerdoAdapter adapter=new recuerdoAdapter(this,getActivity());

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

        // Registrar observer
        mViewModel.buscadorLive().observe(getActivity(), new Observer<List<Recuerdo>>() {
            @Override
            public void onChanged(List<Recuerdo> recuerdos) {
                adapter.setmListaRecuerdos(recuerdos);
            }
        });


        // Preparar RecyclerView
        mRv=v.findViewById(R.id.recyclerBusca);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(llm);
        mRv.setHasFixedSize(true);
        mRv.setAdapter(adapter);

        mCajaBusqueda=v.findViewById(R.id.cajaBusqueda);
        mCajaBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String busqueda="%"+s+"%";
                if (!s.isEmpty()) {
                    //adapter.setmListaRecuerdos(mViewModel.buscador(busqueda)); --> Sin LiveData
                    mViewModel.setTxtBusqueda(busqueda);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //al borrar se eliminan los resultados
                if (s.isEmpty()){
                    adapter.setmListaRecuerdos(null);
                }
                return false;
            }
        });

        return v;
    }


    // Cargar los detalles del Recuerdo al pulsar sobre la tarjeta del RecyclerView
    @Override
    public void onItemClick(Recuerdo recuerdo) {
        Intent intent=new Intent(getActivity(), NuevoRecuerdoActivity.class);
        intent.putExtra("recuerdo", recuerdo);
        startActivity(intent);
    }
}