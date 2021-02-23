package com.idpskuinfo.skuinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.idpskuinfo.skuinfo.data.SkuModel;

import static com.idpskuinfo.skuinfo.db.DatabaseContract.TABLE_NAME;
import static com.idpskuinfo.skuinfo.db.DatabaseContract.NoteColumns.SKUID;
import static com.idpskuinfo.skuinfo.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.idpskuinfo.skuinfo.db.DatabaseContract.NoteColumns.RETAIL_PRICE;
import static com.idpskuinfo.skuinfo.db.DatabaseContract.NoteColumns.SKUTYPE;

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

    public void beginTransaction() {
        database.beginTransaction();
    }
    public void setTransactionSuccess() {
        database.setTransactionSuccessful();
    }
    public void endTransaction() {
        database.endTransaction();
    }
    public void insertTransaction(SkuModel skuModel) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" + SKUID + ", " + DESCRIPTION + ", "+ RETAIL_PRICE  +", "+ SKUTYPE +") VALUES (?, ?, ?, ?)";
        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, skuModel.getSkucode());
        stmt.bindString(2, skuModel.getSkudes());
        stmt.bindString(3, skuModel.getSkuret());
        stmt.bindString(4, skuModel.getSkutype());
        stmt.execute();
        stmt.clearBindings();
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
