package com.agp.mybox.UI.Ajustes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public class FragmentAjustesViewModel extends AndroidViewModel {

    private final String PREFERENCIAS="Preferencias";
    private SharedPreferences mPrefs = getApplication().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

    public FragmentAjustesViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean checkPreferencias(String pref){
        if(mPrefs.contains(pref)){
            return mPrefs.getBoolean(pref, false);
        }
        return false;
    }

    public void setPreferencias(String pref, boolean valor){
        SharedPreferences.Editor editor =mPrefs.edit();
        editor.putBoolean(pref,valor);
        editor.commit();
    }
}
