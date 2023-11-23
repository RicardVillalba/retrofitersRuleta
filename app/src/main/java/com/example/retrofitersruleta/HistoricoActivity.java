package com.example.retrofitersruleta;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistorico;
    private HistoricoAdapter historicoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        recyclerViewHistorico = findViewById(R.id.recyclerView);
        recyclerViewHistorico.setLayoutManager(new LinearLayoutManager(this));

        List<HistoricoItem> historicoItemList = obtenerDatosDeLaBaseDeDatos();
        historicoAdapter = new HistoricoAdapter(historicoItemList);
        recyclerViewHistorico.setAdapter(historicoAdapter);

        Button btnVolver = findViewById(R.id.btnVolver);

        // Agregar un OnClickListener al botón
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción a realizar al hacer clic en el botón (volver a la actividad inicial)
                Intent intent = new Intent(HistoricoActivity.this, InicioActivity.class);
                startActivity(intent);
                finish(); // Opcional: finalizar esta actividad para que no quede en la pila de actividades

            }
        });
    }
    private List<HistoricoItem> obtenerDatosDeLaBaseDeDatos() {
        // Realizar una consulta para obtener el historial de resultados
        WalletDbHelper dbHelper = new WalletDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "historico",
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<HistoricoItem> historicoItemList = new ArrayList<>();

        while (cursor.moveToNext()) {
            // Verificar si la columna existe antes de obtener su índice
            int idIndex = cursor.getColumnIndex("id");
            int nombreJugadorIndex = cursor.getColumnIndex("nombre");
            int monederoIndex = cursor.getColumnIndex("monedero");
            int turnosIndex = cursor.getColumnIndex("turnos");

            if (idIndex != -1 && nombreJugadorIndex != -1 && monederoIndex != -1 && turnosIndex != -1) {
                // Las columnas existen, ahora puedes acceder a sus valores
                int id = cursor.getInt(idIndex);
                String nombre = cursor.getString(nombreJugadorIndex);
                int monedero = cursor.getInt(monederoIndex);
                int turnos = cursor.getInt(turnosIndex);

                HistoricoItem historicoItem = new HistoricoItem(id, nombre, monedero, turnos);
                historicoItemList.add(historicoItem);
            } else {
                // Al menos una de las columnas no existe, manejar la situación según sea necesario
                Log.e("Error", "Al menos una de las columnas no existe en el resultado de la consulta.");
            }
        }

        cursor.close();
        db.close();

        return historicoItemList;
    }
}
