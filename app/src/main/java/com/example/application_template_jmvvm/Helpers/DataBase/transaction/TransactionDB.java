package com.example.application_template_jmvvm.Helpers.DataBase.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseHelper;
import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseOperations;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.activation.ActivationDB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransactionDB extends DatabaseHelper {

    private static TransactionDB sDatabaseHelper;

    public TransactionDB(Context context){
        super(context.getApplicationContext());
    }

    private static void initTransactionTable(SQLiteDatabase db) {
        Map<String, String> tbl_transaction = new LinkedHashMap<>();
        tbl_transaction.put(TransactionCol.col_uuid.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_ulSTN.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_ulGUP_SN.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_batchNo.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_settleNo.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_bCardReadType.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_bTransCode.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_ulAmount.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_ulAmount2.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_baPAN.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baExpDate.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baDate.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baTime.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baTrack2.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baCustomName.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baRspCode.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_isVoid.name(), "INTEGER DEFAULT 0");
        tbl_transaction.put(TransactionCol.col_bInstCnt.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_ulInstAmount.name(), "INTEGER");
        tbl_transaction.put(TransactionCol.col_baTranDate.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baTranDate2.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baHostLogKey.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_stChipData.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_isSignature.name(), "INTEGER DEFAULT 0");
        tbl_transaction.put(TransactionCol.col_stPrintData1.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_stPrintData2.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baVoidDateTime.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_authCode.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_aid.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_aidLabel.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_pinByPass.name(), "INTEGER DEFAULT 0");
        tbl_transaction.put(TransactionCol.col_displayData.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_baCVM.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_isOffline.name(), "INTEGER DEFAULT 0");
        tbl_transaction.put(TransactionCol.col_SID.name(), "TEXT");
        tbl_transaction.put(TransactionCol.col_is_onlinePIN.name(), "INTEGER DEFAULT 0");
        DatabaseOperations.createTable(TRANSACTION_TABLE, tbl_transaction, db);
    }

    @Override
    protected String getTableName() {
        return TRANSACTION_TABLE;
    }

    public static TransactionDB getInstance(Context context){
        if(sDatabaseHelper == null){
            sDatabaseHelper = new TransactionDB(context);
            initTransactionTable(writableSQLite);
        }
        return sDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        initTransactionTable(writableSQLite);
    }

    public void insertTransaction(ContentValues values){
        DatabaseOperations.insert(TRANSACTION_TABLE,writableSQLite,values);
    }

    public boolean isEmpty(){
        return DatabaseOperations.isTableEmpty(TRANSACTION_TABLE,readableSQLite);
    }

    public ArrayList<ContentValues> getTransactionsByCardNo(String cardNo) {
        String queryStr = "SELECT * FROM " + getTableName() + " WHERE " + TransactionCol.col_baPAN.name() + "='" + cardNo + "' AND " + TransactionCol.col_isVoid.name() + " <> '1'";
        return selectTransaction(queryStr);
    }

    private ArrayList<ContentValues> selectTransaction(String queryStr) {
        readableSQLite = getReadableDatabase();
        Cursor cursor = readableSQLite.rawQuery(queryStr, null);
        ArrayList<ContentValues> rows = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                TransactionCol[] colNames = TransactionCol.values();

                for (TransactionCol colName : colNames) {
                    addColumnValue(contentValues, cursor, colName.name());
                }

                rows.add(contentValues);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return rows;
    }
}
