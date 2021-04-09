package com.agp.mybox.UI.Ajustes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agp.mybox.R;

public class fragment_ajustes extends Fragment {

    private FragmentAjustesViewModel mViewModel;

    public static fragment_ajustes newInstance() {
        return new fragment_ajustes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentAjustesViewModel.class);
        // TODO: Use the ViewModel
    }

}