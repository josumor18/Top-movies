package cr.ac.itcr.josuemorales.toppeliculas;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by JosueAndroid on 23/3/2018.
 */

public class Pelicula {
    private String titulo;
    private double puntuacion;
    private int metascore;
    private String dirImagen;

    public Pelicula(String titulo, double puntuacion, int metascore, String dirImagen) {
        this.titulo = titulo;
        this.puntuacion = puntuacion;
        this.metascore = metascore;
        this.dirImagen = dirImagen;
    }

    public String getDirImagen() {
        return dirImagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public int getMetascore() {
        return metascore;
    }
}