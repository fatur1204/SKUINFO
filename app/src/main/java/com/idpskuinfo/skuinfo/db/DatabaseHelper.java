package com.idpskuinfo.skuinfo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    public static String DATABASE_NAME = "skuapp";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_SKUINFO = String.format("CREATE TABLE %s"
                    + " (%s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.NoteColumns.SKUID,
            DatabaseContract.NoteColumns.DESCRIPTION,
            DatabaseContract.NoteColumns.RETAIL_PRICE,
            DatabaseContract.NoteColumns.SKUTYPE
    );

    private static final String SQL_CREATE_TABLE_CURRINFO = String.format("CREATE TABLE %s"
                    + " (%s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME_CURINFO,
            DatabaseContract.CurrColumns.CURRID,
            DatabaseContract.CurrColumns.CURDES,
            DatabaseContract.CurrColumns.CURRDATE,
            DatabaseContract.CurrColumns.CUR_RET
    );

    private static final String SQL_CREATE_TABLE_UPDATEINFO = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME_UPDATE,
            DatabaseContract.UpdateColumns.UPDATEID,
            DatabaseContract.UpdateColumns.UPDATEDATE,
            DatabaseContract.UpdateColumns.UPDATETIME
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SKUINFO);
        db.execSQL(SQL_CREATE_TABLE_CURRINFO);
        db.execSQL(SQL_CREATE_TABLE_UPDATEINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME_CURINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME_UPDATE);
        onCreate(db);
    }
}
