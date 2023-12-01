package com.example.retrofitersruleta;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_MONEDERO;
import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_NOMBRE;
import static com.example.retrofitersruleta.PartidaDatabase.COLUMN_TURNOS;

public class HistoricoActivity extends AppCompatActivity {

    private ListView listView;
    private PartidaDatabase partidaDatabase;
    private ReproductorMusica reproductorMusica;

    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // Inicializar la base de datos de partidas
        partidaDatabase = new PartidaDatabase(this);

        // Inicializar el reproductor de música
        reproductorMusica = ReproductorMusica.getInstance(this);

        // Inicializar ListView
        listView = findViewById(R.id.lista);

        // Inicializar el botón "Volver a Inicio"
        btnVolver = findViewById(R.id.btnVolver);


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el botón, iniciar la actividad InicioActivity
                Intent intent = new Intent(HistoricoActivity.this, InicioActivity.class);
                startActivity(intent);
                finish(); // Cerrar la actividad actual para que el usuario no pueda regresar
            }
        });

        // Obtener y mostrar registros en el ListView
        mostrarRegistrosEnListView();

        reproductorMusica.reproducirMelodiaFondo();

    }

    private void mostrarRegistrosEnListView() {
        // Obtener registros de la base de datos
        Cursor cursor = partidaDatabase.getTop10PartidaRecords();
        ArrayList<String> registros = new ArrayList<>();

        // Recorrer el cursor y agregar registros a la lista
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
                int turnos = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TURNOS));
                int monedero = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONEDERO));

                // Convertir los valores int a String
                String registro = "Nombre: " + nombre + ", Turnos: " + turnos + ", Monedero: " + monedero;
                registros.add(registro);
            } while (cursor.moveToNext());
        }

        // Crear un adaptador personalizado para mostrar los datos en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);

        // Establecer el adaptador en el ListView
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        reproductorMusica.detenerMelodiaFondo();
        ReproductorMusica.getInstance(this).liberarRecursos();
        // Cerrar la base de datos al destruir la actividad
        partidaDatabase.close();
        super.onDestroy();
    }
}
