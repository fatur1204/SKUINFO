package com.idpskuinfo.skuinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.idpskuinfo.skuinfo.db.DatabaseContract.TABLE_NAME;

public class SkuHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dataBaseHelper;
    private static SkuHelper INSTANCE;
    private static SQLiteDatabase database;

    private SkuHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static SkuHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SkuHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseContract.NoteColumns.SKUID + " ASC");
    }

    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.NoteColumns.SKUID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, DatabaseContract.NoteColumns.SKUID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, DatabaseContract.NoteColumns.SKUID + " = ?", new String[]{id});
    }

    public int deleteAll() {
        return database.delete(DATABASE_TABLE, null, null);
    }
}
