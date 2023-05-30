package com.example.application_template_jmvvm.data.service;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.data.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.domain.entity.ResponseCode;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDB;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class TransactionService implements InfoDialogListener {

    private InfoDialog dialog;
    private Observable<ContentValues> observable;
    private Observer<ContentValues> observer;
    public void doInBackground(MainActivity main, Context context, ContentValues values, TransactionViewModel transactionViewModel, TransactionResponseListener responseTransactionResponseListener) {

        dialog = main.showInfoDialog(InfoDialog.InfoType.Progress, "Progress",false);
        observable = Observable.just(values)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        observer = new Observer<ContentValues>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(ContentValues contentValues) {
                for (int i = 0; i <= 11; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.update(InfoDialog.InfoType.Progress,"Progress: "+(i*10));
                }
                Log.i("Values",contentValues.toString());
                dialog.update(InfoDialog.InfoType.Confirmed, "Confirmed");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                OnlineTransactionResponse onlineTransactionResponse = parseResponse(1);
                TransactionResponse transactionResponse = finishTransaction(context,values,onlineTransactionResponse, transactionViewModel);
                responseTransactionResponseListener.onComplete(transactionResponse);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        observable.subscribe(observer);
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

    private TransactionResponse finishTransaction(Context context, ContentValues values, OnlineTransactionResponse onlineTransactionResponse, TransactionViewModel transactionViewModel){
        ContentValues values1 = values;
        values1.put(TransactionCol.col_baRspCode.name(), onlineTransactionResponse.getmResponseCode().toString());
        values1.put(TransactionCol.col_stPrintData1.name(), onlineTransactionResponse.getmTextPrintCode1());
        values1.put(TransactionCol.col_stPrintData2.name(), onlineTransactionResponse.getmTextPrintCode2());
        values1.put(TransactionCol.col_authCode.name(), onlineTransactionResponse.getmAuthCode());
        values1.put(TransactionCol.col_baHostLogKey.name(), onlineTransactionResponse.getmHostLogKey());
        values1.put(TransactionCol.col_displayData.name(), onlineTransactionResponse.getmDisplayData());
        values1.put(TransactionCol.col_bInstCnt.name(), onlineTransactionResponse.getInsCount());
        values1.put(TransactionCol.col_ulInstAmount.name(), onlineTransactionResponse.getInstAmount());
        values1.put(TransactionCol.col_baVoidDateTime.name(), onlineTransactionResponse.getDateTime());
        TransactionEntity transactionEntity = entityCreator(values1);
        transactionViewModel.insertTransaction(transactionEntity); //TODO düzenlenecek
        //TransactionDB.getInstance(context).insertTransaction(values1);
        return new TransactionResponse(onlineTransactionResponse,1,values1);
    }

    private TransactionEntity entityCreator(ContentValues values){
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUuid(values.get(TransactionCol.col_uuid.name()).toString());
        transactionEntity.setUlSTN(values.get(TransactionCol.col_ulSTN.name()).toString());
        //transactionEntity.setBatchNo(Integer.parseInt(values.get(TransactionCol.col_batchNo.name()).toString()));
        //transactionEntity.setSettleNo(Integer.parseInt(values.get(TransactionCol.col_settleNo.name()).toString()));
        transactionEntity.setbCardReadType(Integer.parseInt(values.get(TransactionCol.col_bCardReadType.name()).toString()));
        transactionEntity.setbTransCode(Integer.parseInt(values.get(TransactionCol.col_bTransCode.name()).toString()));
        transactionEntity.setUlAmount(Integer.parseInt(values.get(TransactionCol.col_ulAmount.name()).toString()));
        transactionEntity.setUlAmount2(Integer.parseInt(values.get(TransactionCol.col_ulAmount2.name()).toString()));
        transactionEntity.setBaPAN(values.get(TransactionCol.col_baPAN.name()).toString());
        transactionEntity.setBaExpDate(values.get(TransactionCol.col_baExpDate.name()).toString());
        transactionEntity.setBaDate(values.get(TransactionCol.col_baDate.name()).toString());
        transactionEntity.setBaTime(values.get(TransactionCol.col_baTime.name()).toString());
        transactionEntity.setBaTrack2(values.get(TransactionCol.col_baTrack2.name()).toString());
        //transactionEntity.setBaCustomName(values.get(TransactionCol.col_baCustomName.name()).toString());
        transactionEntity.setBaRspCode(values.get(TransactionCol.col_baRspCode.name()).toString());
        transactionEntity.setIsVoid(0);     //TODO: rspcode incelenecek
        transactionEntity.setbInstCnt(Integer.parseInt(values.get(TransactionCol.col_bInstCnt.name()).toString()));
        transactionEntity.setUlInstAmount(Integer.parseInt(values.get(TransactionCol.col_ulInstAmount.name()).toString()));
        transactionEntity.setBaTranDate(values.get(TransactionCol.col_baTranDate.name()).toString());
        transactionEntity.setBaTranDate2(values.get(TransactionCol.col_baTranDate2.name()).toString());
        transactionEntity.setBaHostLogKey(values.get(TransactionCol.col_baHostLogKey.name()).toString());
        //transactionEntity.setStChipData(values.get(TransactionCol.col_stChipData.name()).toString());
        transactionEntity.setIsSignature(0);
        transactionEntity.setStPrintData1(values.get(TransactionCol.col_stPrintData1.name()).toString());
        transactionEntity.setStPrintData2(values.get(TransactionCol.col_stPrintData2.name()).toString());
        //transactionEntity.setBaVoidDateTime(values.get(TransactionCol.col_baVoidDateTime.name()).toString());
        transactionEntity.setAuthCode(values.get(TransactionCol.col_authCode.name()).toString());
        transactionEntity.setAid(values.get(TransactionCol.col_aid.name()).toString());
        transactionEntity.setAidLabel(values.get(TransactionCol.col_aidLabel.name()).toString());
        transactionEntity.setPinByPass(0);
        transactionEntity.setDisplayData(values.get(TransactionCol.col_displayData.name()).toString());
        transactionEntity.setBaCVM(values.get(TransactionCol.col_baCVM.name()).toString());
        transactionEntity.setIsOffline(0);
        transactionEntity.setSID(values.get(TransactionCol.col_SID.name()).toString());
        transactionEntity.setIsOnlinePIN(0);
        return transactionEntity;
    }
    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}

