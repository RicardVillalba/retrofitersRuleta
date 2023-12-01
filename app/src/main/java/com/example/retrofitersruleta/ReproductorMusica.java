package com.example.retrofitersruleta;

import android.content.Context;
import android.media.MediaPlayer;

public class ReproductorMusica {
    private static ReproductorMusica instancia;
    private MediaPlayer musicaFondo;
    private MediaPlayer sonidoBoton;

    private ReproductorMusica(Context context) {
        musicaFondo = MediaPlayer.create(context, R.raw.melodia_fondo);
        musicaFondo.setLooping(true); // Repetir en bucle

        sonidoBoton = MediaPlayer.create(context, R.raw.pulsar_boton);
    }

    public static ReproductorMusica getInstance(Context context) {
        if (instancia == null) {
            instancia = new ReproductorMusica(context);
        }
        return instancia;
    }

    public void reproducirMelodiaFondo() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.start();
        }
    }

    public void detenerMelodiaFondo() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    public void reproducirSonidoBoton() {
        if (sonidoBoton != null && !sonidoBoton.isPlaying()) {
            sonidoBoton.start();
        }
    }

    public void detenerSonidoBoton() {
        if (sonidoBoton != null && sonidoBoton.isPlaying()) {
            sonidoBoton.pause();
            // Para reiniciar el sonido del botón desde el principio la próxima vez
            sonidoBoton.seekTo(0);
        }
    }

    public void detenerTodo() {
        detenerMelodiaFondo();
        detenerSonidoBoton();
    }

    public void liberarRecursos() {
        if (musicaFondo != null) {
            musicaFondo.release();
            musicaFondo = null;
        }

        if (sonidoBoton != null) {
            sonidoBoton.release();
            sonidoBoton = null;
        }

        instancia = null;
    }
}
