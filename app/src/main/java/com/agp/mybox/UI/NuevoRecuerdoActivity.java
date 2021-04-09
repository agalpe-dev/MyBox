package com.agp.mybox.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.mybox.R;

public class NuevoRecuerdoActivity extends AppCompatActivity {

    private NuevoRecuerdoViewModel mViewModel;
    private Button mBoton_ok, mBoton_cancelar;
    private TextView mTitulo,mComentarios,mEtiquetas;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_recuerdo);
        NuevoRecuerdoActivity.this.setTitle("Nuevo Recuerdo");
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mViewModel=new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(NuevoRecuerdoViewModel.class);

        mTitulo=(TextView)findViewById(R.id.nuevoRecuerdoTitulo);
        mComentarios=(TextView)findViewById(R.id.nuevoRecuerdoTxt);
        mEtiquetas=(TextView)findViewById(R.id.nuevoRecuerdoEtiquetas);

        /*
        Botones eliminados al usar la ActionBar
         */
        /*mBoton_ok=(Button)findViewById(R.id.boton_ok_recuerdo);
        mBoton_cancelar=(Button)findViewById(R.id.boton_cancelar_recuerdo);
        mBoton_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.guardar(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString());
                finish();
            }
        });

        mBoton_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.vista_recuerdo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.botonGuardar:
                mViewModel.guardar(mTitulo.getText().toString(), mComentarios.getText().toString(),mEtiquetas.getText().toString());
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}