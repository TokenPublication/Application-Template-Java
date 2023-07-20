package com.example.application_template_jmvvm.data.database.activation;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

@Entity(tableName = DatabaseInfo.ACT_TABLE)
public class ActivationEntity {
    @ColumnInfo(name = ActivationCols.col_terminalID)
    private String colTerminalId;

    @ColumnInfo(name = ActivationCols.col_merchantID)
    private String colMerchantId;

    @ColumnInfo(name = ActivationCols.col_IP)
    @PrimaryKey
    @NonNull
    private String colIP = "195.87.189.169";

    @ColumnInfo(name = ActivationCols.col_Port)
    private String colPort;

    public String getColTerminalId() {
        return colTerminalId;
    }

    public void setColTerminalId(String colTerminalId) {
        this.colTerminalId = colTerminalId;
    }

    public String getColMerchantId() {
        return colMerchantId;
    }

    public void setColMerchantId(String colMerchantId) {
        this.colMerchantId = colMerchantId;
    }

    @NonNull
    public String getColIP() {
        return colIP;
    }

    public void setColIP(@NonNull String colIP) {
        this.colIP = colIP;
    }

    public String getColPort() {
        return colPort;
    }

    public void setColPort(String colPort) {
        this.colPort = colPort;
    }
}
