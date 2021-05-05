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

        mViewModel.checkPreferencias();

        //mViewModel.poblar();
        /*
        //Opciones de configuración para bottomBar
        //No son necesarias al prescindir de bottomBar y usar SmoothBottomBar
        AppBarConfiguration appBarConfiguration= new AppBarConfiguration.Builder(
                R.id.fragment_inicio,
                R.id.fragment_favoritos,
                R.id.fragment_buscar,
                R.id.fragment_ajustes).build();

         */

        //Recuperar el NavController
        //https://developer.android.com/guide/navigation/navigation-getting-started
        navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController=navHostFragment.getNavController();

        //Problemas, se usa la forma anterior
        //navController= Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Comprobar que existen las rutas
        mViewModel.comprobarRutas();

        //Comprobar que la tabla tiporecuerdo tiene los valores por defecto
        mViewModel.comprobarTablaTipos();



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