package com.example.retrofitersruleta;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.retrofitersruleta.HistoricoActivity;


public class InicioActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Button buttonIniciarJuego;
    private Button buttonVerHistorico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity);

        // Inicializar las vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        buttonIniciarJuego = findViewById(R.id.buttonIniciarJuego);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);


        // Configurar el botón de inicio de juego
        buttonIniciarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre ingresado por el usuario
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
                // Utilizar la clase correcta
                Intent intent = new Intent(InicioActivity.this, HistoricoActivity.class);
                startActivity(intent);
            }
        });
    }
    private void guardarNombreJugador(String nombreJugador) {
        // Almacenar el nombre en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombreJugador", nombreJugador);
        editor.apply();
    }
}
