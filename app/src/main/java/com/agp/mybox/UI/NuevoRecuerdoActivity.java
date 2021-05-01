package com.agp.mybox.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.mybox.Adaptadores.miniRecursoAdapter;
import com.agp.mybox.Adaptadores.recursoAdapter;
import com.agp.mybox.Modelo.POJO.Recuerdo;
import com.agp.mybox.Modelo.Compuestos.RecursoMini;
import com.agp.mybox.Modelo.POJO.Recurso;
import com.agp.mybox.R;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NuevoRecuerdoActivity extends AppCompatActivity {
    static final int PEDIR_CAPTURA_FOTO = 1;

    private NuevoRecuerdoViewModel mViewModel;
    private ImageButton botonFoto, botonArchivo;
    private TextView mTitulo,mComentarios,mEtiquetas;
    private ActionBar actionBar;
    private TextInputLayout tilTitulo;
    private RadioGroup mRadioGroup;
    private RadioButton rbTicket, rbFactura, rbEntrada, rbOtros;
    private RecyclerView rv;
    private miniRecursoAdapter miniAdapter =new miniRecursoAdapter(this);
    private recursoAdapter recursoAdapter=new recursoAdapter(this);

    private Uri fotoUri;
    private Recuerdo mRecuerdo;
    private boolean modoEdicion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_recuerdo);

        // Mostrar botón vuelta atrás y menu (icono guardar)
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mViewModel=new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(NuevoRecuerdoViewModel.class);

        mTitulo=(TextView)findViewById(R.id.nuevoRecuerdoTitulo);
        mComentarios=(TextView)findViewById(R.id.nuevoRecuerdoTxt);
        mEtiquetas=(TextView)findViewById(R.id.nuevoRecuerdoEtiquetas);
        tilTitulo=(TextInputLayout)findViewById(R.id.tl_titulo);
        mRadioGroup=(RadioGroup)findViewById(R.id.radio_grupo);
        rbTicket=(RadioButton)findViewById(R.id.radioTicket);
        rbFactura=(RadioButton)findViewById(R.id.radioFactura);
        rbEntrada=(RadioButton)findViewById(R.id.radioEntrada);
        rbOtros=(RadioButton)findViewById(R.id.radioOtro);

        botonFoto=(ImageButton)findViewById(R.id.botonFoto);
        botonArchivo=(ImageButton)findViewById(R.id.botonArchivo);

        rv=(RecyclerView) findViewById(R.id.rvRecursosmini);
        rv.setLayoutManager(new GridLayoutManager(this,4));
        // rv.setAdapter(miniAdapter);

        // Comprobar si el intent trae un recuerdo (modo edición)
        // Si viene nulo es modo creación nuevo recuerodo

        mRecuerdo=(Recuerdo)getIntent().getSerializableExtra("recuerdo");

        // Edición de Recuerdo. Se cargan los datos del Recuerdo en el formulario
        if (mRecuerdo !=null){
            NuevoRecuerdoActivity.this.setTitle(R.string.editarRecuerdoActivity);
            modoEdicion=true;
            mTitulo.setText(mRecuerdo.getTitulo());
            mComentarios.setText(mRecuerdo.getComentario());
            String txtTipoRecuerdo=mViewModel.getTipoRecuerdoPorId(mRecuerdo.getIdTipoRecuerdo());
            rv.setAdapter(recursoAdapter);

            switch (txtTipoRecuerdo){
                case "ticket":
                    mRadioGroup.check(rbTicket.getId());
                    break;
                case "factura":
                    mRadioGroup.check(rbFactura.getId());
                    break;
                case "entrada":
                    mRadioGroup.check(rbEntrada.getId());
                    break;
                case "otros":
                    mRadioGroup.check(rbOtros.getId());
                    break;
            }

            mViewModel.RecursosDeRecuerdo(mRecuerdo.getId()).observe(this, new Observer<List<Recurso>>() {
                @Override
                public void onChanged(List<Recurso> recursos) {
                    recursoAdapter.setRecursoList(recursos);
                    Log.d("ANTONIO_LIVE", "Llego a actualizar recursos del recuerdo cargado para edición");
                }
            });

            /* TODO - se busca por id fijo, se sustituye (arriba) por busqueda en base datos nombre TipoRecuerdo
            switch (mRecuerdo.getIdTipoRecuerdo()){
                case 1:
                    mRadioGroup.check(rbTicket.getId());
                    break;
                case 2:
                    mRadioGroup.check(rbFactura.getId());
                    break;
                case 3:
                    mRadioGroup.check(rbEntrada.getId());
                    break;
                case 4:
                    mRadioGroup.check(rbOtros.getId());
                    break;
            }*/
        }
        else{
            NuevoRecuerdoActivity.this.setTitle(R.string.nuevoRecuerdoActivity);
            modoEdicion=false;
            rv.setAdapter(miniAdapter);
        }

        // Registrar observador para mensaje de error en edittext de Titulo que emite el ViewModel
        Observer<String> avisoError=new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // comprabar si el aviso tiene texto, si está en blanco se anula el  mensaje de error
                if (s.isEmpty()){
                    tilTitulo.setError(null);
                }else {
                    tilTitulo.setError(s);
                }
            }
        };
        mViewModel.getErrorTitulo().observe(this,avisoError);


        // Listener botonFoton
        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hacerFotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Comprobar que exite cámara para gestionar el intent
                if (hacerFotoIntent.resolveActivity(getPackageManager())!=null){
                    File foto=null;
                    try{
                        foto = mViewModel.crearArchivoImagen();
                    }catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(NuevoRecuerdoActivity.this,"No se ha podido guardar la imagen",Toast.LENGTH_LONG).show();
                    }
                    if (foto!=null){
                        //Uri fotoUri= FileProvider.getUriForFile(getApplicationContext(),"com.agp.mybox.fileprovider",foto);
                        fotoUri= FileProvider.getUriForFile(getApplicationContext(),"com.agp.mybox.fileprovider",foto);
                        //Log.d("ANTONIO",fotoUri.toString());
                        hacerFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                        //startActivityForResult(hacerFotoIntent, PEDIR_CAPTURA_FOTO);
                        //startActivity(hacerFotoIntent);
                        startActivityForResult(hacerFotoIntent,PEDIR_CAPTURA_FOTO);
                    }

                }
            }
        });

        // Observar el LiveData del ViewModel para el recycleview de los minirecursos
        mViewModel.getRecursosMini().observe(this, new Observer<List<RecursoMini>>() {
            @Override
            public void onChanged(List<RecursoMini> recursoMinis) {
                miniAdapter.setRecursos(recursoMinis);
                //Log.d("RECURSOS", Integer.toString(recursoMinis.size()));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PEDIR_CAPTURA_FOTO && resultCode == RESULT_OK){
                mViewModel.crearMiniRecursoLista(fotoUri);
        }
    }

    @Override
    // Inflar menu con la opción de guardar como única (icono disco)
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.vista_recuerdo, menu);
        return true;
    }

    @Override
    // Gestionar selección del menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mViewModel.borrarRecursos(miniAdapter.getRecursos());
                finish();
                break;
            case R.id.botonGuardar:
                // obtener el texto del radiobutton seleccionado para determinar tipoRecuerdo
                RadioButton rb= (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                String mTipoRecuerdoNombre = rb.getText().toString().toLowerCase();
                // Se llama al metodo para crear nuevo Recuerdo o actualizar el existente si
                // estamos en modo edición
                if (modoEdicion) {
                    if (mViewModel.actualizarRecuerdo(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString(), mTipoRecuerdoNombre, mRecuerdo.getId())){
                        finish();
                    }
                } else if (!(modoEdicion)) {
                    if (mViewModel.guardar(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString(), mTipoRecuerdoNombre)){
                        finish();
                    }
                }


        }
        return super.onOptionsItemSelected(item);
    }


}