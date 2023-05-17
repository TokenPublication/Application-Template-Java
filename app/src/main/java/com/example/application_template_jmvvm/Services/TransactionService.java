package com.example.application_template_jmvvm.Services;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.Entity.ICCCard;
import com.example.application_template_jmvvm.Entity.ResponseCode;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionDB;
import com.example.application_template_jmvvm.Responses.OnlineTransactionResponse;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TransactionService implements InfoDialogListener {

    private String testString = "one";
    private Observable<ContentValues> observable;
    private Observer<ContentValues> observer;
    private InfoDialog dialog;
    public void doInBackground(MainActivity main, Context context, ContentValues values) {

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
                for (int i = 0; i <= 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.update(InfoDialog.InfoType.Progress,"Progress: "+(i*10));
                }
                Log.i("Values",contentValues.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                OnlineTransactionResponse onlineTransactionResponse = parseResponse(1);
                finishTransaction(context,values,onlineTransactionResponse);
                Log.i("Complete","Complete");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                main.finish();
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

    private void finishTransaction(Context context, ContentValues values, OnlineTransactionResponse onlineTransactionResponse){
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
        dialog.update(InfoDialog.InfoType.Confirmed, "Confirmed");
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}
