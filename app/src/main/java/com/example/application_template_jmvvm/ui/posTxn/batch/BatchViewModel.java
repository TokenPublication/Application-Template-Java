package com.example.application_template_jmvvm.ui.posTxn.batch;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
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
    private MutableLiveData<Boolean> isPrintedLiveData = new MutableLiveData<>();

    @Inject
    public BatchViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    /**
     * It runs functions in parallel while ui updating dynamically in main thread with InfoDialogs
     * It also calls finishBatchClose functions in parallel in IO thread.
     */
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
                BatchCloseResponse batchCloseResponse = batchRepository.prepareResponse(BatchResult.SUCCESS);
                Intent resultIntent = finishBatchClose(mainActivity, batchCloseResponse, activationRepository, transactionRepository, isAutoBatch);
                mainHandler.post(() -> setIntentLiveData(resultIntent));
            }
        };
        observable.subscribe(observer);
    }

    /**
     * It gets all transactions from transactionViewModel, then makes up slip from printService.
     * Lastly insert this slip to database, to print it again in next day. If it inserts it successfully, ui is updating
     * with Success Message. Finally, update Batch number and resets group number and delete all transactions from Transaction Table.
     * If it is AutoEndOfDay, we send intent to the result. Else, we do not need to prepare intent just slip.
     */
    private Intent finishBatchClose(MainActivity mainActivity, BatchCloseResponse batchCloseResponse, ActivationRepository activationRepository, TransactionRepository transactionRepository, Boolean isAutoBatch) {
        if (batchCloseResponse.getBatchResult().getResultCode() == ResponseCode.SUCCESS.ordinal()) {
            setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getString(R.string.batch_close) + " " + mainActivity.getString(R.string.success)));
            List<Transaction> transactionList = transactionRepository.getAllTransactions();
            String slip = batchRepository.prepareSlip(mainActivity, activationRepository, transactionList, true, false);
            batchRepository.updateBatchSlip(batchRepository.prepareSlip(mainActivity, activationRepository, transactionList, true, true), batchRepository.getBatchNo());
            batchRepository.updateBatchNo();
            transactionRepository.deleteAll();
            new Handler(Looper.getMainLooper()).postDelayed(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.printing_the_receipt))), 1000);
            if (isAutoBatch) {
                return batchRepository.prepareBatchIntent(batchCloseResponse, mainActivity, slip);
            } else {
                batchRepository.printSlip(slip, mainActivity);
            }
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

    public MutableLiveData<Boolean> getIsPrintedLiveData() {
        return isPrintedLiveData;
    }

    public void setIsPrintedLiveData(boolean isPrinted) {
        isPrintedLiveData.postValue(isPrinted);
    }

    public String getPreviousBatchSlip() {
        return batchRepository.getPreviousBatchSlip();
    }

    /**
     * This method for print yesterday's batchClose. It runs in IO Thread for not locking the main thread.
     */
    public void printPreviousBatchSlip(MainActivity mainActivity) {
        Observable<Integer> singleItemObservable = Observable.just(1);
        Disposable disposable = singleItemObservable
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        item -> batchRepository.printSlip(getPreviousBatchSlip(), mainActivity),
                        throwable -> { },
                        () -> setIsPrintedLiveData(true)
                );
    }

    /**
     * This method for print batchClose or transactionList slip.
     */
    public void prepareSlip(MainActivity mainActivity, ActivationRepository activationRepository,
                            List<Transaction> transactionList, boolean isBatch, boolean isCopy) {
        String slip = batchRepository.prepareSlip(mainActivity, activationRepository, transactionList, isBatch, isCopy);
        Observable<Integer> singleItemObservable = Observable.just(1);
        Disposable disposable = singleItemObservable
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        item -> batchRepository.printSlip(slip, mainActivity),
                        throwable -> { },
                        () -> setIsPrintedLiveData(true)
                );
    }
}
