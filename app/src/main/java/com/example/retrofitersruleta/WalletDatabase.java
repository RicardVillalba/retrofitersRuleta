package com.example.retrofitersruleta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WalletDatabase {

    private final WalletDbHelper dbHelper;
    private SQLiteDatabase database;

    public WalletDatabase(Context context) {
        dbHelper = new WalletDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insertar datos en la base de datos
    public void insertWalletData(int id,  String nombre, int monedero, int turnos) {
        open();
        ContentValues values = new ContentValues();
        values.put(WalletContract.WalletEntry.COLUMN_ID, id);
        values.put(WalletContract.WalletEntry.COLUMN_NOMBRE_JUGADOR, nombre);
        values.put(WalletContract.WalletEntry.COLUMN_MONEDERO, monedero);
        values.put(WalletContract.WalletEntry.COLUMN_TURNOS, turnos);

        // Insertar en la tabla de la base de datos
        database.insert(WalletContract.WalletEntry.TABLE_NAME, null, values);

        close();
    }

    // Obtener todos los datos de la base de datos
    public Cursor getAllWalletData() {
        open();
        String[] projection = {
                WalletContract.WalletEntry.COLUMN_ID,
                WalletContract.WalletEntry.COLUMN_NOMBRE_JUGADOR,
                WalletContract.WalletEntry.COLUMN_MONEDERO,
                WalletContract.WalletEntry.COLUMN_TURNOS
        };

        // Consulta a la base de datos
        return database.query(
                WalletContract.WalletEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }
}
