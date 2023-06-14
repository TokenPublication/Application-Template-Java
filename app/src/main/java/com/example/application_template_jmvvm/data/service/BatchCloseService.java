package com.example.application_template_jmvvm.data.service;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.domain.entity.BatchResult;
import com.example.application_template_jmvvm.domain.entity.ResponseCode;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
import com.example.application_template_jmvvm.domain.helper.printHelpers.BatchClosePrintHelper;
import com.example.application_template_jmvvm.domain.helper.printHelpers.PrintServiceBinding;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BatchCloseService implements InfoDialogListener {

    private InfoDialog dialog;
    private Observable<BatchResult> observable;
    private Observer<BatchResult> observer;

    public void doInBackground(MainActivity main, Context context, TransactionViewModel transactionViewModel,
                               BatchViewModel batchViewModel, BatchCloseResponseListener batchCloseResponseListener) {
        BatchResult batchResult = BatchResult.SUCCESS;
        dialog = main.showInfoDialog(InfoDialog.InfoType.Progress, "Progress",false);
        observable = Observable.just(batchResult)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        observer = new Observer<BatchResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(BatchResult batchResult) {
                for (int i = 0; i <= 11; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.update(InfoDialog.InfoType.Progress,"Connecting: "+(i*10));
                }
                dialog.update(InfoDialog.InfoType.Confirmed, "Confirmed");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                BatchCloseResponse batchCloseResponse = finishTransaction(context, transactionViewModel, batchViewModel, batchResult, dialog);
                batchCloseResponseListener.onComplete(batchCloseResponse);
            }
        };
        observable.subscribe(observer);
    }

    private BatchCloseResponse finishTransaction(Context context, TransactionViewModel transactionViewModel,
                                                  BatchViewModel batchViewModel, BatchResult batchResult,
                                                 InfoDialog dialog){
        List<TransactionEntity> transactionList = transactionViewModel.getAllTransactions();
        BatchClosePrintHelper batchClosePrintHelper = new BatchClosePrintHelper();
        PrintServiceBinding printServiceBinding = new PrintServiceBinding();
        String slip = batchClosePrintHelper.batchText(String.valueOf(batchViewModel.getBatchNo()),transactionList,true);
        printServiceBinding.print(slip);
        Log.d("Repetition",slip);
        batchViewModel.updateBatchSlip(slip,batchViewModel.getBatchNo());
        dialog.update(InfoDialog.InfoType.Confirmed, "Grup Kapama Başarılı");
        batchViewModel.updateBatchNo(batchViewModel.getBatchNo());
        batchViewModel.updateGUPSN(0);      //TODO Void için incelenecek.
        transactionViewModel.deleteAllTransactions();
        return new BatchCloseResponse(batchResult,new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault()));
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}

