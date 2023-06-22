package com.example.application_template_jmvvm.domain;

import android.content.ContentValues;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;

public class contentValHelper {
    public static ContentValues contentValCreator(TransactionEntity transactionEntity){
        ContentValues values = new ContentValues();
        values.put(TransactionCols.col_uuid, transactionEntity.getUuid());
        values.put(TransactionCols.col_ulSTN, transactionEntity.getUlSTN());
        values.put(TransactionCols.col_ulGUP_SN, transactionEntity.getUlGUP_SN());
        values.put(TransactionCols.col_batchNo, transactionEntity.getBatchNo());
        values.put(TransactionCols.col_settleNo, transactionEntity.getSettleNo());
        values.put(TransactionCols.col_bCardReadType, transactionEntity.getbCardReadType());
        values.put(TransactionCols.col_bTransCode, transactionEntity.getbTransCode());
        values.put(TransactionCols.col_ulAmount, transactionEntity.getUlAmount());
        values.put(TransactionCols.col_ulAmount2, transactionEntity.getUlAmount2());
        values.put(TransactionCols.col_baPAN, transactionEntity.getBaPAN());
        values.put(TransactionCols.col_baExpDate, transactionEntity.getBaExpDate());
        values.put(TransactionCols.col_baDate, transactionEntity.getBaDate());
        values.put(TransactionCols.col_baTime, transactionEntity.getBaTime());
        values.put(TransactionCols.col_baTrack2, transactionEntity.getBaTrack2());
        values.put(TransactionCols.col_baCustomName, transactionEntity.getBaCustomName());
        values.put(TransactionCols.col_baRspCode, transactionEntity.getBaRspCode());
        values.put(TransactionCols.col_isVoid, transactionEntity.getIsVoid());
        values.put(TransactionCols.col_bInstCnt, transactionEntity.getbInstCnt());
        values.put(TransactionCols.col_baTranDate, transactionEntity.getBaTranDate());
        values.put(TransactionCols.col_baTranDate2, transactionEntity.getBaTranDate2());
        values.put(TransactionCols.col_refNo, transactionEntity.getRefNo());
        values.put(TransactionCols.col_stChipData, transactionEntity.getStChipData());
        values.put(TransactionCols.col_isSignature, transactionEntity.getIsSignature());
        values.put(TransactionCols.col_stPrintData1, transactionEntity.getStPrintData1());
        values.put(TransactionCols.col_stPrintData2, transactionEntity.getStPrintData2());
        values.put(TransactionCols.col_baVoidDateTime, transactionEntity.getBaVoidDateTime());
        values.put(TransactionCols.col_authCode, transactionEntity.getAuthCode());
        values.put(TransactionCols.col_aid, transactionEntity.getAid());
        values.put(TransactionCols.col_aidLabel, transactionEntity.getAidLabel());
        values.put(TransactionCols.col_pinByPass, transactionEntity.getPinByPass());
        values.put(TransactionCols.col_displayData, transactionEntity.getDisplayData());
        values.put(TransactionCols.col_baCVM, transactionEntity.getBaCVM());
        values.put(TransactionCols.col_isOffline, transactionEntity.getIsOffline());
        values.put(TransactionCols.col_SID, transactionEntity.getSID());
        values.put(TransactionCols.col_is_onlinePIN, transactionEntity.getIsOnlinePIN());
        return values;
    }
}
