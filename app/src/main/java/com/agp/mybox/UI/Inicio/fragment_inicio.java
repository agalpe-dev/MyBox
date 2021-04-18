/**
 *
 */
package com.agp.mybox.UI.Inicio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.agp.mybox.Adaptadores.recuerdoAdapter;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.R;
import com.agp.mybox.UI.NuevoRecuerdoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

public class fragment_inicio extends Fragment {

    FragmentInicioViewModel mViewModel;
    FloatingActionButton mFAB;
    RecyclerView mRV;
    recuerdoAdapter adaptador=new recuerdoAdapter();
    List<Recuerdo> listaTrabajo = new ArrayList<>();

    Observer<List<Recuerdo>> listaRecuerdos=new Observer<List<Recuerdo>>() {
        @Override
        public void onChanged(List<Recuerdo> recuerdos) {
            adaptador.setRecuerdos(recuerdos);
            listaTrabajo=recuerdos;
        }
    };

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
                            adaptador.notifyItemRemoved(posicion);
                            // Crear Recuerdo con el recuerdo de lista a eliminar y llamar al ViewHolder
                            Recuerdo r= listaTrabajo.get(posicion);
                            mViewModel.borrarRecuerdo(r);
                        }
                    })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    adaptador.notifyDataSetChanged();
                                    return;
                                }
                            }).show();

            }
        }
    };
    ItemTouchHelper itemTouchHelper=new ItemTouchHelper(itemTouchHelperSimpleCallBack);

    public static fragment_inicio newInstance() {
        return new fragment_inicio();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //acceso a la vista
        final View v=inflater.inflate(R.layout.fragment_inicio,container,false);

        //Intanciar el ViewModel:
        //AndroidViewModel en lugar de ViewModel pues es necesario pasar el contexto de Application para poder usar
        //en el ViewModel el Repository ya que este último requiere Application
        //Creación de ViewModel debe ir junto con el acceso a los componentes o se genera error que impide crear la instacia del ViewModel
        mViewModel= new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentInicioViewModel.class);

        //Acceso a FAB que carga Activity para crear nuevo Recuerdo
        mFAB=(FloatingActionButton) v.findViewById(R.id.boton_crear);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigation.findNavController(v).navigate(R.id.fragment_nuevoRecuerdo);
                startActivity(new Intent(getActivity(), NuevoRecuerdoActivity.class));
            }
        });

        //ReciclerView: Acceso a widget, creación del layoutmanager (vertical), inicializar y setear adaptador
        mRV=(RecyclerView)v.findViewById(R.id.rvRecuerdos);
        /*LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        */
        StaggeredGridLayoutManager sglm=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRV.setLayoutManager(sglm);
        //adaptador=new recuerdoAdapter();
        mRV.setAdapter(adaptador);
        itemTouchHelper.attachToRecyclerView(mRV);





        mViewModel.getTodosRecuerdos().observe(getActivity(),listaRecuerdos);

        /* FUNCIONA BIEN. SE COMENTA PARA PROBAR ARRIBA LO MISMO PERO DECLARANDO EL OBSERVER
        POR SEPARADO Y PODER USARLO LUEGO PARA EL SWIPE DEL RECYCLERVIEW
        //observar el livedata que proporciona el viewmodel
       mViewModel.getTodosRecuerdos().observe(getActivity(), new Observer<List<Recuerdo>>() {
           @Override
           public void onChanged(List<Recuerdo> recuerdos) {
               //Actualizar el ReciclerView
               //Toast.makeText(getActivity(),"observando",Toast.LENGTH_LONG).show();
               //adaptador=new recuerdoAdapter(recuerdos);
               adaptador.setRecuerdos(recuerdos);
           }
       });
        */

        //se devuelve la vista
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}