package com.example.retrofitersruleta;

import android.provider.BaseColumns;

public final class WalletContract {

    private WalletContract() {
        // Private constructor to prevent accidental instantiation
    }

    public static class WalletEntry implements BaseColumns {
        public static final String TABLE_NAME = "Historico";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NOMBRE_JUGADOR = "nombre_jugador";
        public static final String COLUMN_MONEDERO = "monedero";
        public static final String COLUMN_TURNOS = "turnos";

        // Aquí está la constante que necesitas
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NOMBRE_JUGADOR + " TEXT," +
                        COLUMN_MONEDERO + " INTEGER," +
                        COLUMN_TURNOS + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}