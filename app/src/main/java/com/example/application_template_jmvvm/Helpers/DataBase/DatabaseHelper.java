package com.example.application_template_jmvvm.Helpers.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "APP_TEMP";
    private static final int DATABASE_VERSION = 1;

    protected static final String TRANSACTION_TABLE = "TRANSACTIONS";
    protected static final String ACT_TABLE = "ACTIVATION";

    protected static SQLiteDatabase writableSQLite;
    protected static SQLiteDatabase readableSQLite;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        writableSQLite = getWritableDatabase();
        readableSQLite = getReadableDatabase();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("PRAGMA synchronous = 2");
    }

    protected boolean replace(String tableName, ContentValues values) {
        return DatabaseOperations.replace(tableName, writableSQLite, values);
    }

    protected int update(String tableName, ContentValues values, String whereClause) {
        return DatabaseOperations.update(writableSQLite, tableName, whereClause, values);
    }

    protected String getDate() {
        return new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    protected abstract String getTableName();

    protected boolean insert(ContentValues values) {
        return DatabaseOperations.insert(getTableName(), writableSQLite, values);
    }

    protected void addColumnValue(ContentValues contentValues, Cursor cursor, String colName) {
        int index = cursor.getColumnIndex(colName);
        if (index > -1) {
            if (cursor.getType(index) == Cursor.FIELD_TYPE_INTEGER) {
                contentValues.put(colName, cursor.getInt(index));
            } else if (cursor.getType(index) == Cursor.FIELD_TYPE_STRING) {
                contentValues.put(colName, cursor.getString(index));
            } else {
                contentValues.putNull(colName);
            }
        }
    }

    protected List<ContentValues> selectRecord(String queryStr, Enum[] columns) {
        Cursor cursor = readableSQLite.rawQuery(queryStr, null);
        List<ContentValues> rows = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                for (Enum colName : columns) {
                    addColumnValue(contentValues, cursor, colName.name());
                }
                rows.add(contentValues);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return rows;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    private void execSQL(SQLiteDatabase db, String sql) {
        try {
            db.execSQL(sql);
        }
        catch (SQLException e) {
            Log.d("SQL_exception", e.toString());
        }
    }

    public void clearTable() {
        DatabaseOperations.deleteAllRecords(getTableName(), writableSQLite);
    }

    public int clearAllTables() {
        return DatabaseOperations.deleteAllRecords(TRANSACTION_TABLE, writableSQLite);

    }
}
