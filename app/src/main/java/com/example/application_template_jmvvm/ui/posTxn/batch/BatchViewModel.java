package com.example.application_template_jmvvm.ui.posTxn.batch;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.token.uicomponents.infodialog.InfoDialog;

import java.util.List;

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
    private MutableLiveData<InfoDialogData> infoDialogLiveData = new MutableLiveData<>();

    @Inject
    public BatchViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public void BatchCloseRoutine(MainActivity mainActivity, ActivationRepository activationRepository,
                                  TransactionRepository transactionRepository, Boolean isAutoBatch) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.connecting)));
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
                    final String progressText = mainActivity.getString(R.string.connecting) + " " + (i * 10);

                    mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, progressText)));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                Intent resultIntent = finishBatchClose(mainActivity, activationRepository, transactionRepository, isAutoBatch);
                mainHandler.post(() -> setIntentLiveData(resultIntent));
            }
        };
        observable.subscribe(observer);
    }

    private Intent finishBatchClose(MainActivity mainActivity, ActivationRepository activationRepository, TransactionRepository transactionRepository, Boolean isAutoBatch) {
        List<TransactionEntity> transactionList = transactionRepository.getAllTransactions();
        String slip = batchRepository.prepareSlip(mainActivity, activationRepository, batchRepository, transactionList);
        batchRepository.updateBatchSlip(slip, batchRepository.getBatchNo());
        batchRepository.updateBatchNo();
        transactionRepository.deleteAll();
        BatchCloseResponse batchCloseResponse = batchRepository.prepareResponse(mainActivity, this, BatchResult.SUCCESS);
        new Handler(Looper.getMainLooper()).postDelayed(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.printing_the_receipt))), 1000);
        if (isAutoBatch) {
            return batchRepository.prepareBatchIntent(batchCloseResponse, mainActivity, slip);
        } else {
            batchRepository.printSlip(slip, mainActivity);
        }
        return null;
    }

    public BatchRepository getBatchRepository() {
        return batchRepository;
    }

    public int getBatchNo() {
        return batchRepository.getBatchNo();
    }

    public MutableLiveData<Intent> getIntentLiveData() {
        return intentLiveData;
    }

    public void setIntentLiveData(Intent intent) {
        intentLiveData.postValue(intent);
    }

    public MutableLiveData<InfoDialogData> getInfoDialogLiveData() {
        return infoDialogLiveData;
    }

    public void setInfoDialogLiveData(InfoDialogData infoDialogData) {
        infoDialogLiveData.postValue(infoDialogData);
    }

    public String getPreviousBatchSlip() {
        return batchRepository.getPreviousBatchSlip();
    }

    public void printPreviousBatchSlip(MainActivity mainActivity) {
        batchRepository.printSlip(getPreviousBatchSlip(), mainActivity);
    }

    public void deleteAll() {
        batchRepository.deleteAll();
    }
}
