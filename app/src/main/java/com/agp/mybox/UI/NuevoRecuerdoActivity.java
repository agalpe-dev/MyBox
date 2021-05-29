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


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
    static final int PEDIR_ARCHIVO = 2;

    private NuevoRecuerdoViewModel mViewModel;
    private ImageButton botonFoto, botonArchivo;
    private TextView mTitulo,mComentarios,mEtiquetas;
    private ActionBar actionBar;
    private TextInputLayout tilTitulo;
    private RadioGroup mRadioGroup;
    private RadioButton rbTicket, rbFactura, rbEntrada, rbOtros;
    private ProgressBar mBarraOCR;
    private RecyclerView rv;
    // Adapter para RV cuando se está en modo creación Recuerdo (se crean miniRecursos)
    private miniRecursoAdapter miniAdapter =new miniRecursoAdapter(this);
    // Adapter para RV cuando se está en modo edición Recuerdo (se crean Recursos)
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

        // Setear controles
        mTitulo=(TextView)findViewById(R.id.nuevoRecuerdoTitulo);
        mComentarios=(TextView)findViewById(R.id.nuevoRecuerdoTxt);
        mEtiquetas=(TextView)findViewById(R.id.nuevoRecuerdoEtiquetas);
        tilTitulo=(TextInputLayout)findViewById(R.id.tl_titulo);
        mRadioGroup=(RadioGroup)findViewById(R.id.radio_grupo);
        rbTicket=(RadioButton)findViewById(R.id.radioTicket);
        rbFactura=(RadioButton)findViewById(R.id.radioFactura);
        rbEntrada=(RadioButton)findViewById(R.id.radioEntrada);
        rbOtros=(RadioButton)findViewById(R.id.radioOtro);
        mBarraOCR=findViewById(R.id.barraOCR);

        botonFoto=(ImageButton)findViewById(R.id.botonFoto);
        botonArchivo=(ImageButton)findViewById(R.id.botonArchivo);

        // Por defecto, se usa el Adapter para miniRecursos (creación de nuevo recuerdo)
        rv=(RecyclerView) findViewById(R.id.rvRecursosmini);
        rv.setLayoutManager(new GridLayoutManager(this,4));

        // Observer para la barra de progreso de OCR
        mViewModel.estadoOCR().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mBarraOCR.setVisibility(integer);
            }
        });

        // Comprobar si el intent trae un Recuerdo (modo edición, al pusar en el listado de Recuerdos inicial)
        // Si viene nulo es modo creación nuevo recuerodo
        mRecuerdo=(Recuerdo)getIntent().getSerializableExtra("recuerdo");

        // Edición de Recuerdo. Se cargan los datos del Recuerdo en el formulario
        if (mRecuerdo !=null){
            modoEdicion=true;
            NuevoRecuerdoActivity.this.setTitle(R.string.editarRecuerdoActivity);
            mTitulo.setText(mRecuerdo.getTitulo());
            mComentarios.setText(mRecuerdo.getComentario());
            // TODO - Mejora: gestionar la consulta la bd de forma asíncrona con Livedata asociando el resultado a los checkboxes
            String txtTipoRecuerdo=mViewModel.getTipoRecuerdoPorId(mRecuerdo.getIdTipoRecuerdo());
            // Se cambia el Adapter del RV al correspondiente de Recursos ya que se cargan los asociados al Recuerdo (base de datos)
            rv.setAdapter(recursoAdapter); // Adaptador de Recuerdos (cargados de base datos)

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


            // Observer para LiveData con los Recuersos del Recuerdo cargado
            mViewModel.RecursosDeRecuerdo(mRecuerdo.getId()).observe(this, new Observer<List<Recurso>>() {
                @Override
                public void onChanged(List<Recurso> recursos) {
                    recursoAdapter.setRecursoList(recursos);
                }
            });


            // Estamos en modo creación de recuerdo ya que el Recuerdo es nulo
        }
        else{
            NuevoRecuerdoActivity.this.setTitle(R.string.nuevoRecuerdoActivity);
            modoEdicion=false;
            rv.setAdapter(miniAdapter); // Adaptador miniRecuerdo (sólo bitmap thumbnail y uri del archivo de imagen)
        }


        // Registrar observador para mensaje de error en editText de Titulo que emite el ViewModel
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

        // Observador LiveData del mensaje error que emite ViewModel al comprobar si el título está vacío
        mViewModel.getErrorTitulo().observe(this,avisoError);


        // Listener botonFoto -> Llama al intent de la cámara y crea el archivo de imagen.
        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hacerFotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Comprobar que exite cámara para gestionar el intent
                if (hacerFotoIntent.resolveActivity(getPackageManager())!=null){
                    File foto=null;
                    try{
                        foto = mViewModel.crearArchivoImagen(); // el viewmodel crea el archivo en disco
                    }catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(NuevoRecuerdoActivity.this,"No se ha podido guardar la imagen",Toast.LENGTH_LONG).show();
                    }
                    if (foto!=null){
                        fotoUri= FileProvider.getUriForFile(getApplicationContext(),"com.agp.mybox.fileprovider",foto);
                        hacerFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                        startActivityForResult(hacerFotoIntent,PEDIR_CAPTURA_FOTO);
                    }

                }
            }
        });

        // Listener del botón para selección de archivo (no foto)
        botonArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setType("text/plain|image/*|*/pdf");
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent,null),PEDIR_ARCHIVO);
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
        // Filtra la accion (hacer foto) y si es OK llama al viewmodel con el uri del archivo creado
        // para que este actualice el adaptador y cree el thumbnail de la foto tomada
        // Se manda info del modo edición para en el ViewModel crear Recurso o miniRecurso
        if (requestCode==PEDIR_CAPTURA_FOTO && resultCode == RESULT_OK){
            mViewModel.crearMiniRecursoLista(fotoUri, "foto",mRecuerdo);
        }else if (requestCode==PEDIR_ARCHIVO && resultCode == RESULT_OK) {
            Uri uri_archivo=data.getData();
            mViewModel.crearMiniRecursoLista(uri_archivo,"archivo",mRecuerdo);
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
                // Si no se guarda el Recuerdo:
                // En modo Creación: se llama a función que borra del disco los miniRecursos creados (aún no están en bd)
                // En modo Edición: se llama a función que borrar del disco y db Recursos creados.
                if (modoEdicion){ // Modo edición
                    mViewModel.borrarRecursos();
                }else{ // Modo creación
                    mViewModel.borrarMiniRecursos(miniAdapter.getRecursos());
                }
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
                    // Se comprueba si se está haciendo OCR en segundo plano. Si es así, se avisa que no se podrá guardar.
                    // se da la opción de esperar a que finalice OCR o continuar sin guardar OCR.
                    if (mViewModel.getHaciendoOcr()){
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                        dialogo.setTitle(R.string.aviso);
                        dialogo.setMessage(R.string.haciendoOCR);
                        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Se llama al método para guardar el recuerdo. Allí se vuelve a comprobar si se está haciendo OCR y en caso positivo no se espera a sus resultados
                                if (mViewModel.guardarRecuerdo(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString(), mTipoRecuerdoNombre,false)){
                                    finish();
                                }
                            }
                        })
                                .setNegativeButton(R.string.boton_cancelar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                }).show();

                    }else {
                        if (mViewModel.guardarRecuerdo(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString(), mTipoRecuerdoNombre, true)){
                            finish();
                        }
                    }
                }


        }
        return super.onOptionsItemSelected(item);
    }



}