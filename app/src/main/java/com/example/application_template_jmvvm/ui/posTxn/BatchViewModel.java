package com.example.application_template_jmvvm.ui.posTxn;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.domain.printHelpers.BatchClosePrintHelper;
import com.token.uicomponents.infodialog.InfoDialog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class BatchViewModel extends ViewModel {

    public BatchRepository batchRepository;
    private MutableLiveData<Intent> intentLiveData  = new MutableLiveData<>();
    private MutableLiveData<String> showDialogLiveData = new MutableLiveData<>();

    @Inject
    public BatchViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public void BatchCloseRoutine(MainActivity mainActivity, ActivationRepository activationRepository,
                                  TransactionRepository transactionRepository) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setShowDialogLiveData("Progress");
        Observable<Boolean> observable = Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(Boolean bool) {
                for (int i = 0; i <= 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    final String progressText = "Progress: " + (i * 10);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setShowDialogLiveData(progressText);
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                Intent resultIntent = finishBatchClose(mainActivity, activationRepository, transactionRepository);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setIntentLiveData(resultIntent);
                    }
                });
            }
        };
        observable.subscribe(observer);
    }

    private Intent finishBatchClose(MainActivity mainActivity, ActivationRepository activationRepository, TransactionRepository transactionRepository) {
        List<TransactionEntity> transactionList = transactionRepository.getAllTransactions();
        String slip = batchRepository.prepareSlip(activationRepository, batchRepository, transactionList);
        batchRepository.updateBatchSlip(slip, batchRepository.getBatchNo());
        batchRepository.updateBatchNo();
        transactionRepository.deleteAll();
        BatchCloseResponse batchCloseResponse = batchRepository.prepareResponse(this, BatchResult.SUCCESS, new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault()));
        return batchRepository.prepareBatchIntent(batchCloseResponse, mainActivity, slip);
    }

    public BatchRepository getBatchRepository() {
        return batchRepository;
    }

    public MutableLiveData<Intent> getIntentLiveData() {
        return intentLiveData;
    }

    public void setIntentLiveData(Intent intent) {
        intentLiveData.postValue(intent);
    }

    public MutableLiveData<String> getShowDialogLiveData() {
        return showDialogLiveData;
    }

    public void setShowDialogLiveData(String text) {
        showDialogLiveData.postValue(text);
    }

    public int getBatchNo() {
        return batchRepository.getBatchNo();
    }

    public int getGroupSN() {
        return batchRepository.getGroupSN();
    }

    public String getPreviousBatchSlip() {
        return batchRepository.getPreviousBatchSlip();
    }

    public void updateBatchNo() {
        batchRepository.updateBatchNo();
    }

    public void updateBatchSlip(String batchSlip, Integer batchNo) {
        batchRepository.updateBatchSlip(batchSlip, batchNo);
    }

    public void deleteAll() {
        batchRepository.deleteAll();
    }

}