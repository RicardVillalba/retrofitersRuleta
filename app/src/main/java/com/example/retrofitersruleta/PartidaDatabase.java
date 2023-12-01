package com.example.retrofitersruleta;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PartidaDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "partidas_database";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PARTIDAS = "partidas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TURNOS = "turnos";
    public static final String COLUMN_MONEDERO = "monedero";

    public PartidaDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PARTIDAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_TURNOS + " INTEGER, " +
                COLUMN_MONEDERO + " INTEGER);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTIDAS);
        onCreate(db);
    }

    public void addPartidaRecord(String nombre, int turnos, int monedero) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_TURNOS, turnos);
        values.put(COLUMN_MONEDERO, monedero);
        db.insert(TABLE_PARTIDAS, null, values);
        db.close();
    }

    public Cursor getTop10PartidaRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NOMBRE, COLUMN_TURNOS, COLUMN_MONEDERO};
        String orderBy = COLUMN_MONEDERO + " DESC";
        String limit = "10";
        return db.query(TABLE_PARTIDAS, columns, null, null, null, null, orderBy, limit);
    }

}

