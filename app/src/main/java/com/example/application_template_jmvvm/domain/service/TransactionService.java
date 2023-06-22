package com.example.application_template_jmvvm.domain.service;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.model.response.TransactionResponse;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TransactionService {

    private Observable<ContentValues> observable;
    private Observer<ContentValues> observer;
    private TransactionCode transactionCode;
    public void doInBackground(ContentValues values, TransactionCode transactionCode, TransactionViewModel transactionViewModel,
                               BatchRepository batchRepository, TransactionResponseListener responseTransactionResponseListener) {

        this.transactionCode = transactionCode;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        transactionViewModel.setShowDialogLiveData("Progress");
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
                for (int i = 0; i <= 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    final String progressText = "Progress: " + (i * 10);

                    // Use the handler to post a Runnable on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            transactionViewModel.setShowDialogLiveData(progressText); //TODO bakılacak.
                        }
                    });
                }
                Log.i("Values",contentValues.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                OnlineTransactionResponse onlineTransactionResponse = parseResponse(1);
                TransactionResponse transactionResponse = finishTransaction(values,onlineTransactionResponse, batchRepository, transactionViewModel.getTransactionRepository());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        transactionViewModel.setTransactionResponseLiveData(transactionResponse);
                    }
                });
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

    private OnlineTransactionResponse parseResponse(int transactionCode) {  //Repository
        OnlineTransactionResponse onlineTransactionResponse = new OnlineTransactionResponse();
        onlineTransactionResponse.setmResponseCode(ResponseCode.SUCCESS);
        onlineTransactionResponse.setmTextPrintCode1("Test Print 1");
        onlineTransactionResponse.setmTextPrintCode2("Test Print 2");
        onlineTransactionResponse.setmAuthCode(String.valueOf((int) (Math.random() * 100000)));
        onlineTransactionResponse.setmRefNo(String.valueOf((int) (Math.random() * 100000000)));
        onlineTransactionResponse.setmDisplayData("Display Data");
        onlineTransactionResponse.setmKeySequenceNumber("3");
        onlineTransactionResponse.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return onlineTransactionResponse;
    }

    private TransactionResponse finishTransaction(ContentValues values, OnlineTransactionResponse onlineTransactionResponse,
                                                  BatchRepository batchRepository, TransactionRepository transactionRepository){
        ContentValues contentValues = values;
        contentValues.put(TransactionCols.col_baRspCode, onlineTransactionResponse.getmResponseCode().toString());
        contentValues.put(TransactionCols.col_stPrintData1, onlineTransactionResponse.getmTextPrintCode1());
        contentValues.put(TransactionCols.col_stPrintData2, onlineTransactionResponse.getmTextPrintCode2());
        contentValues.put(TransactionCols.col_authCode, onlineTransactionResponse.getmAuthCode());
        contentValues.put(TransactionCols.col_refNo, onlineTransactionResponse.getmRefNo());
        contentValues.put(TransactionCols.col_displayData, onlineTransactionResponse.getmDisplayData());
        contentValues.put(TransactionCols.col_batchNo, batchRepository.getBatchNo());
        TransactionEntity transactionEntity = entityCreator(contentValues);
        transactionEntity.setBatchNo(batchRepository.getBatchNo());
        if (transactionCode != TransactionCode.VOID){
            transactionEntity.setUlGUP_SN(batchRepository.getGroupSN());
            transactionRepository.insertTransaction(transactionEntity);
            batchRepository.updateGUPSN(batchRepository.getGroupSN());
        }
        else {
            transactionEntity.setUlGUP_SN(Integer.parseInt(values.get(TransactionCols.col_ulGUP_SN).toString()));
            transactionRepository.setVoid(transactionEntity.getUlGUP_SN(),transactionEntity.getBaDate(),transactionEntity.getSID());
        }   //TODO slip hazırlanacak.
        return new TransactionResponse(onlineTransactionResponse,1,contentValues);
    }

    private TransactionEntity entityCreator(ContentValues values){
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUuid(values.get(TransactionCols.col_uuid).toString());
        transactionEntity.setUlSTN(values.get(TransactionCols.col_ulSTN).toString());
        transactionEntity.setUlAmount(Integer.parseInt(values.get(TransactionCols.col_ulAmount).toString()));
        switch (transactionCode) {
            case MATCHED_REFUND:
                transactionEntity.setUlAmount2(Integer.parseInt(values.get(TransactionCols.col_ulAmount2).toString()));
                transactionEntity.setAuthCode(values.get(TransactionCols.col_authCode).toString());
                transactionEntity.setBaTranDate2(values.get(TransactionCols.col_baTranDate2).toString());
                break;
            case CASH_REFUND:
                transactionEntity.setUlAmount2(Integer.parseInt(values.get(TransactionCols.col_ulAmount2).toString()));
                break;
            case INSTALLMENT_REFUND:
                transactionEntity.setbInstCnt(Integer.parseInt(values.get(TransactionCols.col_bInstCnt).toString()));
                break;
            default:
                // Handle other refund types or provide a default behavior
                break;
        }
        transactionEntity.setbCardReadType(Integer.parseInt(values.get(TransactionCols.col_bCardReadType).toString()));
        transactionEntity.setbTransCode(Integer.parseInt(values.get(TransactionCols.col_bTransCode).toString()));
        transactionEntity.setBaPAN(values.get(TransactionCols.col_baPAN).toString());
        transactionEntity.setBaExpDate(values.get(TransactionCols.col_baExpDate).toString());
        transactionEntity.setBaDate(values.get(TransactionCols.col_baDate).toString());
        transactionEntity.setBaTime(values.get(TransactionCols.col_baTime).toString());
        transactionEntity.setBaTrack2(values.get(TransactionCols.col_baTrack2).toString());
        transactionEntity.setBaRspCode(values.get(TransactionCols.col_baRspCode).toString());
        transactionEntity.setIsVoid(0);     //TODO: rspcode incelenecek
        transactionEntity.setBaTranDate(values.get(TransactionCols.col_baTranDate).toString());
        transactionEntity.setRefNo(values.get(TransactionCols.col_refNo).toString());
        transactionEntity.setIsSignature(0);
        transactionEntity.setStPrintData1(values.get(TransactionCols.col_stPrintData1).toString());
        transactionEntity.setStPrintData2(values.get(TransactionCols.col_stPrintData2).toString());
        transactionEntity.setAuthCode(values.get(TransactionCols.col_authCode).toString());
        transactionEntity.setAid(values.get(TransactionCols.col_aid).toString());
        transactionEntity.setAidLabel(values.get(TransactionCols.col_aidLabel).toString());
        transactionEntity.setPinByPass(0);
        transactionEntity.setDisplayData(values.get(TransactionCols.col_displayData).toString());
        transactionEntity.setBaCVM(values.get(TransactionCols.col_baCVM).toString());
        transactionEntity.setIsOffline(0);
        transactionEntity.setSID(values.get(TransactionCols.col_SID).toString());
        transactionEntity.setIsOnlinePIN(0);
        return transactionEntity;
    }

}

