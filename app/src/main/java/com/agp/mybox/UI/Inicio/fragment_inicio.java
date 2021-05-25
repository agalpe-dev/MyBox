/**
 *
 */
package com.agp.mybox.UI.Inicio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.agp.mybox.Adaptadores.recuerdoAdapter;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.R;
import com.agp.mybox.UI.NuevoRecuerdoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class fragment_inicio extends Fragment implements recuerdoAdapter.ItemClickListener{

    FragmentInicioViewModel mViewModel;
    FloatingActionButton mFAB;
    RecyclerView mRV;
    recuerdoAdapter adaptador=new recuerdoAdapter(this, getActivity());
    List<Recuerdo> listaTrabajo = new ArrayList<>();
    CheckBox cbTicket, cbFactura, cbEntrada, cbOtros;

    // Se incluye aquí para poder hacer uso de ItemTouchHelper y poder gestionar deslizar elemento
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

        // Acceso a la vista
        final View v=inflater.inflate(R.layout.fragment_inicio,container,false);

        // Intanciar el ViewModel:
        // AndroidViewModel en lugar de ViewModel pues es necesario pasar el contexto de Application para poder usar
        // en el ViewModel el Repository ya que este último requiere Application
        // Creación de ViewModel debe ir junto con el acceso a los componentes o se genera error que impide crear la instacia del ViewModel
        mViewModel= new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(FragmentInicioViewModel.class);

        // Acceso a FAB que carga Activity para crear nuevo Recuerdo
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
        mRV.setAdapter(adaptador);
        itemTouchHelper.attachToRecyclerView(mRV);

        // Observer para lista de recuerdos
        // más arriba se crea el observer para poder usarlo en deslizar el cardview
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

        // Observar del livedata que comparte el adapter del Recyclerview con el id del
        // Recuerdo al que se ha pulsado en borón de favorito
        adaptador.getFavoritoOn().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mViewModel.favoritoON(integer);
            }
        });

        adaptador.getFavoritoOff().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mViewModel.favoritoOFF(integer);
            }
        });

        // Gestión de los checkboxes
        cbTicket=(CheckBox)v.findViewById(R.id.checkTicket);
        cbFactura=(CheckBox)v.findViewById(R.id.checkFactura);
        cbEntrada=(CheckBox)v.findViewById(R.id.checkEntrada);
        cbOtros=(CheckBox)v.findViewById(R.id.checkOtros);

        cbTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbTicket.isChecked()){
                    // Buscar en tabla TiposRecuerdo el id
                    int i=mViewModel.getIdTipoRecuerdo(cbTicket.getText().toString());
                    mViewModel.getRecuerdosPorTipo(i);
                }
            }
        });

        //se devuelve la vista
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onItemClick(Recuerdo recuerdo) {
        // Toast.makeText(getActivity(),"Pulsado: " + recuerdo.getTitulo().toString(),Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getActivity(),NuevoRecuerdoActivity.class);
        intent.putExtra("recuerdo", recuerdo);
        startActivity(intent);
    }
}