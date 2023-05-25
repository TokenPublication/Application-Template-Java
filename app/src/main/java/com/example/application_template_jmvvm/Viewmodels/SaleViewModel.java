package com.example.application_template_jmvvm.Viewmodels;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.Entity.ICCCard;
import com.example.application_template_jmvvm.Entity.PaymentTypes;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Responses.TransactionResponse;
import com.example.application_template_jmvvm.Services.TransactionResponseListener;
import com.example.application_template_jmvvm.Services.TransactionService;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;

public class SaleViewModel extends ViewModel {
    private MutableLiveData<String> mActionName = new MutableLiveData<>();
    private MainActivity main;

    public void setMainActivity(MainActivity main){
        this.main = main;
    }

    public LiveData<String> getActionName() {
        return mActionName;
    }

    public void setActionName(String actionName) {
        mActionName.setValue(actionName);
    }

    //TODO View state

    private MutableLiveData<TransactionResponse> transactionResponseLiveData = new MutableLiveData<>();

    public LiveData<TransactionResponse> getTransactionResponseLiveData() {
        return transactionResponseLiveData;
    }

    public void performSaleTransaction(ICCCard card, TransactionService transactionService, Context context, String uuid) {
        ContentValues values = prepareContentValues(card, uuid);
        final TransactionResponse[] transactionResponse = {new TransactionResponse()};
        transactionService.doInBackground(main, context, values, new TransactionResponseListener() {
            @Override
            public void onComplete(TransactionResponse response) {
                transactionResponse[0] = response;
                transactionResponseLiveData.postValue(transactionResponse[0]);
            }
        });
        //
    }

    private ContentValues prepareContentValues(ICCCard card, String uuid) {
        ContentValues values = new ContentValues();  //TODO RxJava content value
        values.put(TransactionCol.col_uuid.name(), uuid);
        values.put(TransactionCol.col_bCardReadType.name(), card.getmCardReadType());
        values.put(TransactionCol.col_bTransCode.name(), 55);
        values.put(TransactionCol.col_ulAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_ulAmount2.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baPAN.name(), card.getmCardNumber());
        values.put(TransactionCol.col_baExpDate.name(), card.getmExpireDate());
        values.put(TransactionCol.col_baDate.name(), card.getDateTime().substring(0, 8));
        values.put(TransactionCol.col_baTime.name(), card.getDateTime().substring(8));
        values.put(TransactionCol.col_baTrack2.name(), card.getmTrack2Data());
        values.put(TransactionCol.col_baCustomName.name(), card.getmTrack1CustomerName());
        values.put(TransactionCol.col_baRspCode.name(), 3);
        values.put(TransactionCol.col_bInstCnt.name(), 10);
        values.put(TransactionCol.col_ulInstAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baTranDate.name(), card.getDateTime());
        values.put(TransactionCol.col_baTranDate2.name(), card.getDateTime());
        values.put(TransactionCol.col_baHostLogKey.name(), "1020304050");
        values.put(TransactionCol.col_authCode.name(), "10203040");
        values.put(TransactionCol.col_aid.name(), card.getAID2());
        values.put(TransactionCol.col_aidLabel.name(), card.getAIDLabel());
        values.put(TransactionCol.col_baCVM.name(), card.getCVM());
        values.put(TransactionCol.col_SID.name(), card.getSID());
        return values;
    }

}