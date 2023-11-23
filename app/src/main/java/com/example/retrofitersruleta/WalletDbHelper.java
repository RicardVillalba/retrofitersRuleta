package com.example.retrofitersruleta;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WalletDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Wallet.db";

    private Context mContext;


    public WalletDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.mContext = context;
        }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Crear la tabla para almacenar los resultados
        db.execSQL(WalletContract.WalletEntry.SQL_CREATE_ENTRIES);
        copyDatabaseFromAssets();



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Manejar actualizaciones de la base de datos si es necesario
        db.execSQL(WalletContract.WalletEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void copyDatabaseFromAssets() {
        try {

            AssetManager assetManager = mContext.getAssets();
            InputStream inputStream = mContext.getAssets().open(DATABASE_NAME);
            String outFileName = mContext.getDatabasePath(DATABASE_NAME).getPath();
            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;


            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
