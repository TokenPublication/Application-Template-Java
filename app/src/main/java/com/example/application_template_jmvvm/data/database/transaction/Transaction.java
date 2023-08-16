package com.example.application_template_jmvvm.data.database.transaction;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

@Entity(tableName = DatabaseInfo.TRANSACTION_TABLE)
public class Transaction {
    @ColumnInfo(name = TransactionCols.col_uuid)
    public String uuid;

    @ColumnInfo(name = TransactionCols.col_ulSTN)
    public int ulSTN;

    @PrimaryKey
    @ColumnInfo(name = TransactionCols.col_ulGUP_SN)
    public int ulGUP_SN;

    @ColumnInfo(name = TransactionCols.col_batchNo)
    public int batchNo;

    @ColumnInfo(name = TransactionCols.col_receiptNo)
    public String receiptNo;

    @ColumnInfo(name = TransactionCols.col_ZNO)
    public String ZNO;

    @ColumnInfo(name = TransactionCols.col_bCardReadType)
    public int bCardReadType;

    @ColumnInfo(name = TransactionCols.col_bTransCode)
    public int bTransCode;

    @ColumnInfo(name = TransactionCols.col_ulAmount)
    public int ulAmount;

    @ColumnInfo(name = TransactionCols.col_ulAmount2)
    public int ulAmount2;

    @ColumnInfo(name = TransactionCols.col_baPAN)
    public String baPAN;

    @ColumnInfo(name = TransactionCols.col_baExpDate)
    public String baExpDate;

    @ColumnInfo(name = TransactionCols.col_baTrack2)
    public String baTrack2;

    @ColumnInfo(name = TransactionCols.col_baCustomerName)
    public String baCustomerName;

    @ColumnInfo(name = TransactionCols.col_baRspCode)
    public int baRspCode;

    @ColumnInfo(name = TransactionCols.col_isVoid)
    public int isVoid;

    @ColumnInfo(name = TransactionCols.col_bInstCnt)
    public int bInstCnt;

    @ColumnInfo(name = TransactionCols.col_baTranDate)
    public String baTranDate;

    @ColumnInfo(name = TransactionCols.col_baTranDate2)
    public String baTranDate2;

    @ColumnInfo(name = TransactionCols.col_refNo)
    public String refNo;

    @ColumnInfo(name = TransactionCols.col_stChipData)
    public String stChipData;

    @ColumnInfo(name = TransactionCols.col_stPrintData)
    public String stPrintData;

    @ColumnInfo(name = TransactionCols.col_baVoidDateTime)
    public String baVoidDateTime;

    @ColumnInfo(name = TransactionCols.col_authCode)
    public String authCode;

    @ColumnInfo(name = TransactionCols.col_aid)
    public String aid;

    @ColumnInfo(name = TransactionCols.col_aidLabel)
    public String aidLabel;

    @ColumnInfo(name = TransactionCols.col_pinByPass)
    public int pinByPass;

    @ColumnInfo(name = TransactionCols.col_displayData)
    public String displayData;

    @ColumnInfo(name = TransactionCols.col_baCVM)
    public String baCVM;

    @ColumnInfo(name = TransactionCols.col_AC)
    private String AC;

    @ColumnInfo(name = TransactionCols.Col_CID)
    private String CID;

    @ColumnInfo(name = TransactionCols.Col_ATC)
    private String ATC;

    @ColumnInfo(name = TransactionCols.Col_TVR)
    private String TVR;

    @ColumnInfo(name = TransactionCols.Col_TSI)
    private String TSI;

    @ColumnInfo(name = TransactionCols.Col_AIP)
    private String AIP;

    @ColumnInfo(name = TransactionCols.col_isOffline)
    public int isOffline;

    @ColumnInfo(name = TransactionCols.col_SID)
    public String SID;

    @ColumnInfo(name = TransactionCols.col_is_onlinePIN)
    public int isOnlinePIN;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUlSTN(int ulSTN) {
        this.ulSTN = ulSTN;
    }

    public void setUlGUP_SN(int ulGUP_SN) {
        this.ulGUP_SN = ulGUP_SN;
    }

    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getZNO() {
        return ZNO;
    }

    public void setZNO(String ZNO) {
        this.ZNO = ZNO;
    }

    public void setbCardReadType(int bCardReadType) {
        this.bCardReadType = bCardReadType;
    }

    public void setbTransCode(int bTransCode) {
        this.bTransCode = bTransCode;
    }

    public void setUlAmount(int ulAmount) {
        this.ulAmount = ulAmount;
    }

    public void setUlAmount2(int ulAmount2) {
        this.ulAmount2 = ulAmount2;
    }

    public void setBaPAN(String baPAN) {
        this.baPAN = baPAN;
    }

    public void setBaExpDate(String baExpDate) {
        this.baExpDate = baExpDate;
    }

    public void setBaTrack2(String baTrack2) {
        this.baTrack2 = baTrack2;
    }

    public void setBaCustomerName(String baCustomName) {
        this.baCustomerName = baCustomName;
    }

    public void setBaRspCode(int baRspCode) {
        this.baRspCode = baRspCode;
    }

    public void setIsVoid(int isVoid) {
        this.isVoid = isVoid;
    }

    public void setbInstCnt(int bInstCnt) {
        this.bInstCnt = bInstCnt;
    }

    public void setBaTranDate(String baTranDate) {
        this.baTranDate = baTranDate;
    }

    public void setBaTranDate2(String baTranDate2) {
        this.baTranDate2 = baTranDate2;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public void setStChipData(String stChipData) {
        this.stChipData = stChipData;
    }

    public void setStPrintData(String stPrintData) {
        this.stPrintData = stPrintData;
    }

    public void setBaVoidDateTime(String baVoidDateTime) {
        this.baVoidDateTime = baVoidDateTime;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setAidLabel(String aidLabel) {
        this.aidLabel = aidLabel;
    }

    public void setPinByPass(int pinByPass) {
        this.pinByPass = pinByPass;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

    public void setBaCVM(String baCVM) {
        this.baCVM = baCVM;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public void setIsOnlinePIN(int isOnlinePIN) {
        this.isOnlinePIN = isOnlinePIN;
    }

    public String getUuid() {
        return uuid;
    }

    public int getUlSTN() {
        return ulSTN;
    }

    public int getUlGUP_SN() {
        return ulGUP_SN;
    }

    public int getBatchNo() {
        return batchNo;
    }

    public int getbCardReadType() {
        return bCardReadType;
    }

    public int getbTransCode() {
        return bTransCode;
    }

    public int getUlAmount() {
        return ulAmount;
    }

    public int getUlAmount2() {
        return ulAmount2;
    }

    public String getBaPAN() {
        return baPAN;
    }

    public String getBaExpDate() {
        return baExpDate;
    }

    public String getBaTrack2() {
        return baTrack2;
    }

    public String getBaCustomerName() {
        return baCustomerName;
    }

    public int getBaRspCode() {
        return baRspCode;
    }

    public int getIsVoid() {
        return isVoid;
    }

    public int getbInstCnt() {
        return bInstCnt;
    }

    public String getBaTranDate() {
        return baTranDate;
    }

    public String getBaTranDate2() {
        return baTranDate2;
    }

    public String getRefNo() {
        return refNo;
    }

    public String getStChipData() {
        return stChipData;
    }

    public String getStPrintData() {
        return stPrintData;
    }

    public String getBaVoidDateTime() {
        return baVoidDateTime;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getAid() {
        return aid;
    }

    public String getAidLabel() {
        return aidLabel;
    }

    public int getPinByPass() {
        return pinByPass;
    }

    public String getDisplayData() {
        return displayData;
    }

    public String getBaCVM() {
        return baCVM;
    }

    public String getAC() {
        return AC;
    }

    public void setAC(String AC) {
        this.AC = AC;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getATC() {
        return ATC;
    }

    public void setATC(String ATC) {
        this.ATC = ATC;
    }

    public String getTVR() {
        return TVR;
    }

    public void setTVR(String TVR) {
        this.TVR = TVR;
    }

    public String getTSI() {
        return TSI;
    }

    public void setTSI(String TSI) {
        this.TSI = TSI;
    }

    public String getAIP() {
        return AIP;
    }

    public void setAIP(String AIP) {
        this.AIP = AIP;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public String getSID() {
        return SID;
    }

    public int getIsOnlinePIN() {
        return isOnlinePIN;
    }
}
