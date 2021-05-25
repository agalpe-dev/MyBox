package com.agp.mybox.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class Backups extends AppCompatActivity {

    private String archivoBackup=null;
    private Integer posicionLista =null;
    private Utils utils=new Utils();
    private ArrayList<String> listaArchivos;
    private ActionBar actionBar;
    private ListView mLista;
    private BackupsViewModel mViewModel;
    private Button mBtBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backups);
        Backups.this.setTitle("Selecciona Backup");

        mViewModel= new ViewModelProvider(this).get(BackupsViewModel.class);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mBtBorrar=findViewById(R.id.btBorrarBackup);

        mLista=findViewById(R.id.listaBackups);

        // Acciones del listview
        mLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(getApplicationContext(),listaArchivos.get(i) + " - " + i, Toast.LENGTH_LONG).show();
                mViewModel.setArchivoBackup(listaArchivos.get(i));
                mViewModel.setPosicion(i);
            }
        });

        // Obtener los datos del intent
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        if (extras!=null){
            if (extras.containsKey("LISTA_ARCHIVOS")){
                ArrayList<String> lista=new ArrayList<>();
                listaArchivos=extras.getStringArrayList("LISTA_ARCHIVOS");
                Collections.sort(listaArchivos);
            }
        }


        // Observer del archivoBackup
        mViewModel.getArchivoBackup().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                archivoBackup=s;
            }
        });

        // Poner el listado de archivos en el listview
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.backup_item,R.id.txtItemBackup,listaArchivos);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,listaArchivos);
        mLista.setAdapter(adapter);

        // Botón borrar archivo backup
        mBtBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solo se muestra el aviso si se ha seleccionado algún elemento de la lista
                if (archivoBackup!=null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder((Backups.this));
                    dialog.setTitle(R.string.aviso);
                    dialog.setMessage("Vas a eliminar el archivo de copia de seguridad. No podrás volver a recuperarlo o restaurarlo.\n\n¿Estás seguro?");
                    dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.borrarArchivoBackup(archivoBackup);
                            listaArchivos.remove(mViewModel.getPosicion());
                            adapter.notifyDataSetChanged();
                        }
                    })
                            .setNegativeButton(R.string.boton_cancelar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).show();
                }
            }
        });


    }

    // Inflar menu con la opción de guardar como única (icono disco)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_backup, menu);
        return true;
    }

    // Gestionar selección del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // Se pulsa la flecha de vuelta y se descarta la operación
                finish();
                break;
            case R.id.botonGuardar:
                // comprobar que se ha seleccionado archivo de backup
                if (archivoBackup !=null){
                    // devolución del nombre del archivo seleccionado al fragment de ajustes que inició la activity
                    Intent intentRetorno=new Intent();
                    intentRetorno.putExtra("ARCHIVO",archivoBackup);
                    setResult(Activity.RESULT_OK,intentRetorno);
                    finish();
                }
                else{
                    Toast.makeText(this,"Debes seleccionar un archivo.",Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}