package com.example.application_template_jmvvm.Helpers.DataBase.activation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseHelper;
import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseOperations;
import com.tokeninc.deviceinfo.DeviceInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationDB extends DatabaseHelper {

    private static ActivationDB sDatabaseHelper;
    private static Map<String,String> tbl_activation;
    private static final String IP = "192.80.125.169";
    private static final String Port = "1051";

    private static void initActivationTable(SQLiteDatabase db) {
        tbl_activation = new LinkedHashMap<>();
        tbl_activation.put(ActivationCol.col_terminalID.name(), "TEXT");
        tbl_activation.put(ActivationCol.col_merchantID.name(), "TEXT");
        tbl_activation.put(ActivationCol.col_IP.name(), "TEXT");
        tbl_activation.put(ActivationCol.col_Port.name(), "TEXT");
        DatabaseOperations.createTable(ACT_TABLE, tbl_activation, db);
    }

    private ActivationDB(Context context){
        super(context.getApplicationContext());
    }

    @Override
    protected String getTableName() {
        return ACT_TABLE;
    }

    public static ActivationDB getInstance(Context context){
        if(sDatabaseHelper == null){
            sDatabaseHelper = new ActivationDB(context);
            initActivationTable(writableSQLite);
            sDatabaseHelper.initHostSettings();
        }
        return sDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initActivationTable(db);
    }

    public boolean insertConnection(String IP, String port) {
        ContentValues values = new ContentValues();
        values.put(ActivationCol.col_IP.name(), IP);
        values.put(ActivationCol.col_Port.name(), port);
        DatabaseOperations.deleteAllRecords(ACT_TABLE, writableSQLite);
        return DatabaseOperations.insert(ACT_TABLE, writableSQLite, values);
    }

    public void initHostSettings() {
        int count = Integer.parseInt(DatabaseOperations.
                query(readableSQLite,"SELECT COUNT(*) FROM " + ACT_TABLE));
        if (count <= 0) {
            insertConnection(IP, Port);
        }
    }

    public void insertActivation(Context context, String terminalId, String merchantId) {
        ContentValues values = new ContentValues();
        values.put(ActivationCol.col_terminalID.name(), terminalId);
        values.put(ActivationCol.col_merchantID.name(), merchantId);
        DatabaseOperations.update(writableSQLite, ACT_TABLE, "1=1", values);
        if (merchantId != null && terminalId != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                DeviceInfo deviceInfo = new DeviceInfo(context);
                deviceInfo.setBankParams(new DeviceInfo.DeviceInfoBankParamsSetterHandler() {
                    @Override
                    public void onReturn(boolean var1) {
                        if (var1){
                            Log.i("setBankParams", "Success");
                        }
                        else {
                            Log.i("setBankParams", "Error");
                        }
                        deviceInfo.unbind();
                    }
                }, terminalId, merchantId);
            });
        }
    }

    public String getMerchantId() {
        String query = "SELECT " + ActivationCol.col_merchantID.name() + " FROM " + ACT_TABLE + " LIMIT 1";
        return DatabaseOperations.query(readableSQLite, query);
    }

    public String getTerminalId() {
        String query = "SELECT " + ActivationCol.col_terminalID.name() + " FROM " + ACT_TABLE + " LIMIT 1";
        return DatabaseOperations.query(readableSQLite, query);
    }

    public boolean isActivated() {
        String merchantId = getMerchantId();
        return merchantId != null && !merchantId.trim().isEmpty();
    }

    public String getHostIP() {
        String query = "SELECT " + ActivationCol.col_IP.name() + " FROM " + ACT_TABLE + " LIMIT 1";
        return DatabaseOperations.query(readableSQLite, query);
    }

    public String getHostPort() {
        String query = "SELECT " + ActivationCol.col_Port.name() + " FROM " + ACT_TABLE + " LIMIT 1";
        return DatabaseOperations.query(readableSQLite, query);
    }
}
