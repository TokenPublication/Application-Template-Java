package com.example.application_template_jmvvm.data.database.activation;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

@Dao
public interface ActivationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertActivation(Activation activation);

    @Query("UPDATE " + DatabaseInfo.ACT_TABLE + " SET " + ActivationCols.col_IP + " = :ip, " + ActivationCols.col_Port + " = :port " + "WHERE " + ActivationCols.col_IP + " = :old_ip")
    void updateConnection(String ip, String port, String old_ip);

    @Query("UPDATE " + DatabaseInfo.ACT_TABLE + " SET " + ActivationCols.col_terminalID + " = :terminalId, " + ActivationCols.col_merchantID + " = :merchantId " + "WHERE " + ActivationCols.col_IP + " = :ip")
    void updateActivation(String terminalId, String merchantId, String ip);

    @Query("SELECT " + ActivationCols.col_merchantID + " FROM " + DatabaseInfo.ACT_TABLE + " LIMIT 1")
    String getMerchantId();

    @Query("SELECT " + ActivationCols.col_terminalID + " FROM " + DatabaseInfo.ACT_TABLE + " LIMIT 1")
    String getTerminalId();

    @Query("SELECT " + ActivationCols.col_IP + " FROM " + DatabaseInfo.ACT_TABLE + " LIMIT 1")
    String getHostIP();

    @Query("SELECT " + ActivationCols.col_Port + " FROM " + DatabaseInfo.ACT_TABLE + " LIMIT 1")
    String getHostPort();
}
