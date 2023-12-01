package com.example.retrofitersruleta;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


public class InicioActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Button buttonIniciarJuego;
    private Button buttonVerHistorico;
    private Switch switchAudio;
    private ReproductorMusica reproductorMusica;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity);
        switchAudio = findViewById(R.id.switchAudio);
        reproductorMusica = ReproductorMusica.getInstance(this);


        // Inicializar las vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        buttonIniciarJuego = findViewById(R.id.buttonIniciarJuego);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);

        // Configurar el botón de inicio de juego
        buttonIniciarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();                // Obtener el nombre ingresado por el usuario
                String nombreJugador = editTextNombre.getText().toString();

                // Verificar si se ingresó un nombre
                if (!TextUtils.isEmpty(nombreJugador)) {
                    // Crear un intent para pasar el nombre a la actividad principal (MainActivity)
                    Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                    intent.putExtra("nombre_jugador", nombreJugador);

                    // Iniciar la actividad principal
                    startActivity(intent);

                    // Finalizar esta actividad para que el usuario no pueda regresar a ella
                    finish();                }
                else {
                    // Mostrar un mensaje si no se ingresó un nombre
                    editTextNombre.setError("Ingresa tu nombre");
                }
            }

        });

        buttonVerHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();                // Obtener el nombre ingresado por el usuario
                // Utilizar la clase correcta
                Intent intent = new Intent(InicioActivity.this, HistoricoActivity.class);
                startActivity(intent);
            }
        });
        // Configurar el Switch para activar/desactivar audio
        switchAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si el Switch está activado, reproducir el sonido del botón y la melodía de fondo
                ReproductorMusica.getInstance(InicioActivity.this).reproducirSonidoBoton();
                ReproductorMusica.getInstance(InicioActivity.this).reproducirMelodiaFondo();
            } else {
                // Si el Switch está desactivado, detener la reproducción del sonido del botón y la melodía de fondo
                ReproductorMusica.getInstance(InicioActivity.this).detenerTodo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reproductorMusica.reproducirMelodiaFondo();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        reproductorMusica.detenerMelodiaFondo();
        // Liberar recursos del ReproductorMusica cuando la actividad se destruye
        ReproductorMusica.getInstance(this).liberarRecursos();
    }

}
