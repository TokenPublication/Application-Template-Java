package com.example.application_template_jmvvm.Services;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.Entity.ResponseCode;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionDB;
import com.example.application_template_jmvvm.Responses.OnlineTransactionResponse;
import com.example.application_template_jmvvm.Responses.TransactionResponse;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.TrampolineScheduler;
import io.reactivex.schedulers.Schedulers;


public class TransactionService implements InfoDialogListener {

    private InfoDialog dialog;
    public TransactionResponse doInBackground(MainActivity main, Context context, ContentValues values) {
        TransactionResponse[] transactionResponse = new TransactionResponse[1];
        TransactionResponse response = performTransaction(values,transactionResponse, context, main).blockingGet();
        //TODO Info Dialog incelenecek.
        return response;
    }

    public Single<TransactionResponse> performTransaction(ContentValues values,TransactionResponse[] transactionResponse, Context context, MainActivity main) {
        return Single.create(new SingleOnSubscribe<TransactionResponse>() {
                    @Override
                    public void subscribe(SingleEmitter<TransactionResponse> emitter) throws Exception {
                        OnlineTransactionResponse onlineTransactionResponse = parseResponse(1);
                        transactionResponse[0] = finishTransaction(context,values,onlineTransactionResponse);
                        emitter.onSuccess(transactionResponse[0]);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
    private OnlineTransactionResponse parseResponse(int transactionCode) {
        OnlineTransactionResponse onlineTransactionResponse = new OnlineTransactionResponse();
        onlineTransactionResponse.setmResponseCode(ResponseCode.SUCCESS);
        onlineTransactionResponse.setmTextPrintCode1("Test Print 1");
        onlineTransactionResponse.setmTextPrintCode2("Test Print 2");
        onlineTransactionResponse.setmAuthCode(String.valueOf((int) (Math.random() * 100000)));
        onlineTransactionResponse.setmHostLogKey(String.valueOf((int) (Math.random() * 100000000)));
        onlineTransactionResponse.setmDisplayData("Display Data");
        onlineTransactionResponse.setmKeySequenceNumber("3");
        onlineTransactionResponse.setInsCount(0);
        onlineTransactionResponse.setInstAmount(0);
        onlineTransactionResponse.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return onlineTransactionResponse;
    }

    private TransactionResponse finishTransaction(Context context, ContentValues values, OnlineTransactionResponse onlineTransactionResponse){
        values.put(TransactionCol.col_baRspCode.name(), onlineTransactionResponse.getmResponseCode().toString());
        values.put(TransactionCol.col_stPrintData1.name(), onlineTransactionResponse.getmTextPrintCode1());
        values.put(TransactionCol.col_stPrintData2.name(), onlineTransactionResponse.getmTextPrintCode2());
        values.put(TransactionCol.col_authCode.name(), onlineTransactionResponse.getmAuthCode());
        values.put(TransactionCol.col_baHostLogKey.name(), onlineTransactionResponse.getmHostLogKey());
        values.put(TransactionCol.col_displayData.name(), onlineTransactionResponse.getmDisplayData());
        values.put(TransactionCol.col_bInstCnt.name(), onlineTransactionResponse.getInsCount());
        values.put(TransactionCol.col_ulInstAmount.name(), onlineTransactionResponse.getInstAmount());
        values.put(TransactionCol.col_baVoidDateTime.name(), onlineTransactionResponse.getDateTime());
        TransactionDB.getInstance(context).insertTransaction(values);
        return new TransactionResponse(onlineTransactionResponse,1,values);
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}
