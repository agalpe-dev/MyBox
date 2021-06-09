package com.agp.mybox.UI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.agp.mybox.R;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    private SmoothBottomBar barra_menu;
    private NavHostFragment navHostFragment;
    private NavController navController;
    mainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barra_menu=(SmoothBottomBar) findViewById(R.id.barra_menu);

        mViewModel=new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(mainActivityViewModel.class);

        // Comprueba SharedPreferences y establece valores por defecto
        mViewModel.checkPreferencias();


        //Recuperar el NavController
        //https://developer.android.com/guide/navigation/navigation-getting-started
        navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController=navHostFragment.getNavController();


        //Comprobar que existen las rutas
        mViewModel.comprobarRutas();

        //Comprobar que la tabla tiporecuerdo tiene los valores por defecto
        new Thread(new Runnable() {
            @Override
            public void run() {
                mViewModel.comprobarTablaTipos();
            }
        }).start();


        //Pruebas Asset
        mViewModel.checkDatosOCR();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Para poder usar SmoothBottomBar con Navigator (NavController)
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_barra, menu);
        barra_menu.setupWithNavController(menu,navController);
        return true;
    }
}