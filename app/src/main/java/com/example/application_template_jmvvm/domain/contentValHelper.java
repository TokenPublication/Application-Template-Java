package com.example.application_template_jmvvm.domain;

import android.content.ContentValues;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;

public class contentValHelper {
    public static ContentValues contentValCreator(TransactionEntity transactionEntity){
        ContentValues values = new ContentValues();
        values.put(TransactionCol.col_uuid.name(), transactionEntity.getUuid());
        values.put(TransactionCol.col_ulSTN.name(), transactionEntity.getUlSTN());
        values.put(TransactionCol.col_ulGUP_SN.name(), transactionEntity.getUlGUP_SN());
        values.put(TransactionCol.col_batchNo.name(), transactionEntity.getBatchNo());
        values.put(TransactionCol.col_settleNo.name(), transactionEntity.getSettleNo());
        values.put(TransactionCol.col_bCardReadType.name(), transactionEntity.getbCardReadType());
        values.put(TransactionCol.col_bTransCode.name(), transactionEntity.getbTransCode());
        values.put(TransactionCol.col_ulAmount.name(), transactionEntity.getUlAmount());
        values.put(TransactionCol.col_ulAmount2.name(), transactionEntity.getUlAmount2());
        values.put(TransactionCol.col_baPAN.name(), transactionEntity.getBaPAN());
        values.put(TransactionCol.col_baExpDate.name(), transactionEntity.getBaExpDate());
        values.put(TransactionCol.col_baDate.name(), transactionEntity.getBaDate());
        values.put(TransactionCol.col_baTime.name(), transactionEntity.getBaTime());
        values.put(TransactionCol.col_baTrack2.name(), transactionEntity.getBaTrack2());
        values.put(TransactionCol.col_baCustomName.name(), transactionEntity.getBaCustomName());
        values.put(TransactionCol.col_baRspCode.name(), transactionEntity.getBaRspCode());
        values.put(TransactionCol.col_isVoid.name(), transactionEntity.getIsVoid());
        values.put(TransactionCol.col_bInstCnt.name(), transactionEntity.getbInstCnt());
        values.put(TransactionCol.col_ulInstAmount.name(), transactionEntity.getUlInstAmount());
        values.put(TransactionCol.col_baTranDate.name(), transactionEntity.getBaTranDate());
        values.put(TransactionCol.col_baTranDate2.name(), transactionEntity.getBaTranDate2());
        values.put(TransactionCol.col_baHostLogKey.name(), transactionEntity.getBaHostLogKey());
        values.put(TransactionCol.col_stChipData.name(), transactionEntity.getStChipData());
        values.put(TransactionCol.col_isSignature.name(), transactionEntity.getIsSignature());
        values.put(TransactionCol.col_stPrintData1.name(), transactionEntity.getStPrintData1());
        values.put(TransactionCol.col_stPrintData2.name(), transactionEntity.getStPrintData2());
        values.put(TransactionCol.col_baVoidDateTime.name(), transactionEntity.getBaVoidDateTime());
        values.put(TransactionCol.col_authCode.name(), transactionEntity.getAuthCode());
        values.put(TransactionCol.col_aid.name(), transactionEntity.getAid());
        values.put(TransactionCol.col_aidLabel.name(), transactionEntity.getAidLabel());
        values.put(TransactionCol.col_pinByPass.name(), transactionEntity.getPinByPass());
        values.put(TransactionCol.col_displayData.name(), transactionEntity.getDisplayData());
        values.put(TransactionCol.col_baCVM.name(), transactionEntity.getBaCVM());
        values.put(TransactionCol.col_isOffline.name(), transactionEntity.getIsOffline());
        values.put(TransactionCol.col_SID.name(), transactionEntity.getSID());
        values.put(TransactionCol.col_is_onlinePIN.name(), transactionEntity.getIsOnlinePIN());
        return values;
    }
}
