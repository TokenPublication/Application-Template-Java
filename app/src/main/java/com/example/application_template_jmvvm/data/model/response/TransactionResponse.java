package com.example.application_template_jmvvm.data.model.response;

import android.content.ContentValues;

public class TransactionResponse {
    OnlineTransactionResponse onlineTransactionResponse;
    Integer transactionCode;
    ContentValues contentValues;

    public TransactionResponse(){}

    public TransactionResponse(OnlineTransactionResponse onlineTransactionResponse, Integer transactionCode,
                               ContentValues contentvalues){
        this.onlineTransactionResponse = onlineTransactionResponse;
        this.transactionCode = transactionCode;
        this.contentValues = contentvalues;
    }

    public OnlineTransactionResponse getOnlineTransactionResponse() {
        return onlineTransactionResponse;
    }

    public void setOnlineTransactionResponse(OnlineTransactionResponse onlineTransactionResponse) {
        this.onlineTransactionResponse = onlineTransactionResponse;
    }

    public Integer getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(Integer transactionCode) {
        this.transactionCode = transactionCode;
    }

    public ContentValues getContentValues() {
        return contentValues;
    }

    public void setContentValues(ContentValues contentValues) {
        this.contentValues = contentValues;
    }

}
