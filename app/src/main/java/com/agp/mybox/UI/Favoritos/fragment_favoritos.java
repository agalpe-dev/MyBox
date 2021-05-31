package com.agp.mybox.UI.Favoritos;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import java.util.ArrayList;
import java.util.List;

public class fragment_favoritos extends Fragment implements recuerdoAdapter.ItemClickListener{

    FloatingActionButton mFAB;
    private FragmentFavoritosViewModel mViewModel;
    private RecyclerView mRv;
    List<Recuerdo> listaTrabajo = new ArrayList<>();
    private recuerdoAdapter adapter=new recuerdoAdapter(this,getActivity());

    // Para la gestión del deslizamiento y borrado en RecyclerView
    // Se define deslizamiento a la derecha para eliminar
    ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Obtener posición de la tarjeta deslizada
            final int posicion=viewHolder.getAdapterPosition();

            // Eliminar a la derecha
            if (direction==ItemTouchHelper.RIGHT){
                AlertDialog.Builder aviso=new AlertDialog.Builder(getContext());
                aviso.setTitle(R.string.tituloBorrar);
                aviso.setMessage(R.string.avisoBorrar);
                aviso.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.notifyItemRemoved(posicion);
                        // Crear Recuerdo con el recuerdo de lista a eliminar y llamar al ViewHolder
                        Recuerdo r= listaTrabajo.get(posicion);
                        mViewModel.borrarRecuerdo(r);
                    }
                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        }).show();

            }
        }
    };
    ItemTouchHelper itemTouchHelper=new ItemTouchHelper(itemTouchHelperSimpleCallBack);


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
        itemTouchHelper.attachToRecyclerView(mRv);

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
                listaTrabajo=recuerdos;
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

    // Cargar detalles del Recuerdo
    @Override
    public void onItemClick(Recuerdo recuerdo) {
        Intent intent=new Intent(getActivity(), NuevoRecuerdoActivity.class);
        intent.putExtra("recuerdo", recuerdo);
        startActivity(intent);
    }
}