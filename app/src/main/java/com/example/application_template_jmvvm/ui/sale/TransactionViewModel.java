package com.example.application_template_jmvvm.ui.sale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.token.uicomponents.infodialog.InfoDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * This viewModel holds LiveData variables for communicate with UI layer and repository for Transaction operations.
 */
@HiltViewModel
public class TransactionViewModel extends ViewModel {
    private TransactionRepository transactionRepository;
    private MutableLiveData<Intent> intentLiveData  = new MutableLiveData<>();
    private MutableLiveData<InfoDialogData> infoDialogLiveData = new MutableLiveData<>();

    @Inject
    public TransactionViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    /**
     * It runs functions in parallel while ui updating dynamically in main thread
     * Additionally, in IO thread it parses the response and make it OnlineTransactionResponse
     * then call Finish Transaction operation with that parameter.
     * @param bundle can contain refundInfo. It can be null.
     * @param transactionEntity comes from Void flow and it can be null.
     * @param uuid comes from Payment Gateway in Sale Transaction. It can be null
     * @param isGIB it is true in GIB operations and false for normal operations. For sale operation,
     * it is null because of sale operations always send intents.
     */
    public void TransactionRoutine(ICCCard card, String uuid, MainActivity mainActivity, TransactionEntity transactionEntity,
                                   Bundle bundle, TransactionCode transactionCode, ActivationRepository activationRepository,
                                   BatchRepository batchRepository, Boolean isGIB) {
        TransactionViewModel transactionViewModel = this;
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
                OnlineTransactionResponse onlineTransactionResponse = transactionRepository.parseResponse(transactionViewModel, mainActivity);
                batchRepository.updateSTN();
                Intent resultIntent = finishTransaction(card, uuid, mainActivity, transactionEntity, bundle, transactionCode, onlineTransactionResponse, activationRepository, batchRepository, isGIB);
                mainHandler.post(() -> setIntentLiveData(resultIntent));
            }
        };
        observable.subscribe(observer);
    }

    /**
     * Create a entity respect to parameters, then if it is Void update transaction as changing isVoid and VoidDateAndTime
     * else -> insert that entity to Transaction table and update Group Serial Number of batch table.
     * Update dialog with confirmation code if database operations result without an error.
     */
    private Intent finishTransaction(ICCCard card, String uuid, MainActivity mainActivity, TransactionEntity transactionEntity,
                                     Bundle bundle, TransactionCode transactionCode, OnlineTransactionResponse onlineTransactionResponse,
                                     ActivationRepository activationRepository, BatchRepository batchRepository, Boolean isGIB) {
        if (transactionCode != TransactionCode.VOID) {
            transactionEntity = transactionRepository.entityCreator(card, uuid, bundle, onlineTransactionResponse, transactionCode);
            transactionEntity.setUlSTN(batchRepository.getSTN());
            transactionEntity.setBatchNo(batchRepository.getBatchNo());
            transactionEntity.setUlGUP_SN(batchRepository.getGroupSN());
            transactionRepository.insertTransaction(transactionEntity);
            batchRepository.updateGUPSN();
        }
        else {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + " " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            transactionRepository.setVoid(transactionEntity.getUlGUP_SN(), date, transactionEntity.getSID());
        }
        if (isGIB != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.printing_the_receipt))), 1000);
            if (isGIB) {
                return transactionRepository.prepareIntent(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, onlineTransactionResponse.getmResponseCode());
            } else {
                transactionRepository.prepareSlip(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, false);
            }
        } else {
            String ZNO = bundle.getString("ZNO");
            String receiptNo = bundle.getString("ReceiptNo");
            return transactionRepository.prepareSaleIntent(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, onlineTransactionResponse.getmResponseCode(), ZNO, receiptNo);
        }
        return null;
    }

    public void prepareDummyResponse(ActivationRepository activationRepository, BatchRepository batchRepository, MainActivity mainActivity,
                                     Integer price, ResponseCode code, Boolean hasSlip, SlipType slipType, String cardNo, String ownerName, int paymentType) {
        transactionRepository.prepareDummyResponse(this, activationRepository, batchRepository, mainActivity,
                                                    price, code, hasSlip, slipType, cardNo, ownerName, paymentType);
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

    public List<TransactionEntity> getTransactionsByCardNo(String cardNo) {
        return transactionRepository.getTransactionsByCardNo(cardNo);
    }

    public List<TransactionEntity> getTransactionsByRefNo(String refNo) {
        return transactionRepository.getTransactionsByRefNo(refNo);
    }

    public List<TransactionEntity> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    /**
     * This method for print slip related to transactionEntity and transactionCode.
     * It runs in IO Thread for not locking the main thread.
     */
    public void prepareSlip(ActivationRepository activationRepository, BatchRepository batchRepository, MainActivity mainActivity,
                                    TransactionEntity transactionEntity, TransactionCode transactionCode, boolean isCopy) {
        Observable<Integer> singleItemObservable = Observable.just(1);
        Disposable disposable = singleItemObservable
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        item -> transactionRepository.prepareSlip(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, isCopy),
                        throwable -> { },
                        () -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, ""))
                );
    }

    public boolean isVoidListEmpty() {
        return transactionRepository.isEmptyVoid();
    }

    public boolean isTransactionListEmpty() {
        return transactionRepository.isEmpty();
    }
}
