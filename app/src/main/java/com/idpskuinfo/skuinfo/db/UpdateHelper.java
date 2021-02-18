package com.idpskuinfo.skuinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.idpskuinfo.skuinfo.db.DatabaseContract.TABLE_NAME_UPDATE;

public class UpdateHelper {
    private static final String DATABASE_TABLE = TABLE_NAME_UPDATE;
    private static DatabaseHelper dataBaseHelper;
    private static UpdateHelper INSTANCE;
    private static SQLiteDatabase database;

    private UpdateHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static UpdateHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UpdateHelper(context);
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
                DatabaseContract.UpdateColumns.UPDATEID + " ASC");
    }

    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.UpdateColumns.UPDATEID + " = ?",
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
        return database.update(DATABASE_TABLE, values, DatabaseContract.UpdateColumns.UPDATEID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, DatabaseContract.UpdateColumns.UPDATEID + " = ?", new String[]{id});
    }
}
