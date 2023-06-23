package com.example.application_template_jmvvm.domain.service;

import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.domain.printHelpers.BatchClosePrintHelper;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
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
                BatchCloseResponse batchCloseResponse = finishTransaction(main, context, transactionViewModel, batchViewModel, batchResult, dialog);
                batchCloseResponseListener.onComplete(batchCloseResponse);
            }
        };
        observable.subscribe(observer);
    }

    private BatchCloseResponse finishTransaction(MainActivity mainActivity, Context context, TransactionViewModel transactionViewModel,
                                                  BatchViewModel batchViewModel, BatchResult batchResult, InfoDialog dialog){
        List<TransactionEntity> transactionList = transactionViewModel.getAllTransactions();
        BatchClosePrintHelper batchClosePrintHelper = new BatchClosePrintHelper();
        /*String slip = batchClosePrintHelper.batchText(String.valueOf(batchViewModel.getBatchNo()),new ActivationRepository(), transactionList,true);
        print(slip, mainActivity);
        Log.d("Repetition",slip);
        batchViewModel.updateBatchSlip(slip,batchViewModel.getBatchNo());*/
        dialog.update(InfoDialog.InfoType.Confirmed, "Grup Kapama Başarılı");
        transactionViewModel.deleteAllTransactions();
        return new BatchCloseResponse(batchResult,new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault()));
    }

    public void print(String printText, MainActivity mainActivity) {
        StyledString styledText = new StyledString();
        styledText.addStyledText(printText);
        styledText.finishPrintingProcedure();
        styledText.print(PrinterService.getService(mainActivity.getApplicationContext()));
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}

