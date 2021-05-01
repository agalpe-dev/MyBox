package com.agp.mybox.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.agp.mybox.R;
import com.agp.mybox.Utils.Utils;

import java.io.IOException;

public class ImagenActivity extends AppCompatActivity {
    Utils utils=new Utils();
    ImageView imagen;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);
        ActionBar actionBar=getSupportActionBar();

        if (actionBar!=null){
            actionBar.hide();
        }
        imagen=(ImageView)findViewById(R.id.imagenGrande);

        Uri uri=Uri.parse(getIntent().getStringExtra("uriImagen"));

        Bitmap b=null;

        try {
            b = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            b = utils.rotarImagen(b,90);
            imagen.setImageBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}