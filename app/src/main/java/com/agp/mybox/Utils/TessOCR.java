package com.agp.mybox.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by Antonio Gálvez on 26/05/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class TessOCR {

    private final TessBaseAPI mTess;

    public TessOCR(Context context, String idioma){
        mTess=new TessBaseAPI();
        String ruta=context.getFilesDir()+ File.separator+"tesseract"+File.separator;
        Log.d("AGP_OCR", ruta);
        Log.d("AGP_OCR", idioma);
        mTess.init(ruta,idioma);

    }

    public String getBitmapOCR(Bitmap bitmap){
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public String getFileOCR(File file){
        mTess.setImage(file);
        return mTess.getUTF8Text();
    }

    public void onDestroy(){
        if (mTess!=null){
            mTess.end();
        }
    }

}
