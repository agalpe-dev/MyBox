package com.agp.mybox.Utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Antonio Gálvez on 12/06/2021.
 * MyBox - Proyecto Ilerna
 * agalpe@gmail.com
 */
public class UtilsTest {

    private Utils mUtils;

    @Before
    public void setup(){
        mUtils=new Utils();
        assertNotNull(mUtils);
    }

    @Test
    public void getTimestamp() {
        assertTrue(mUtils.getTimestamp()>Long.parseLong("10000000000"));
    }

    @Test
    public void timestampToFecha() {
        Long n=Long.parseLong("1623494725000");
        assertTrue(mUtils.timestampToFecha(n)=="12/06/2021 10:45");
    }

    @Test
    public void etiquetasLista() {
    }

    @Test
    public void rotarImagen() {
    }

    @Test
    public void copiarArchivo() {
    }

    @Test
    public void borrarArchivoDisco() {
    }
}