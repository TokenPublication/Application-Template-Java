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

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    public void TransactionRoutine(ICCCard card, String uuid, MainActivity mainActivity, TransactionEntity transactionEntity,
                                   Bundle bundle, TransactionCode transactionCode, ActivationRepository activationRepository, BatchRepository batchRepository) {
        TransactionViewModel transactionViewModel = this;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, "Progress"));
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
                OnlineTransactionResponse onlineTransactionResponse = transactionRepository.parseResponse(transactionViewModel);
                Intent resultIntent = finishTransaction(card, uuid, mainActivity, transactionEntity,
                                                        bundle, transactionCode, onlineTransactionResponse, activationRepository, batchRepository);
                mainHandler.post(() -> setIntentLiveData(resultIntent));
            }
        };
        observable.subscribe(observer);
    }

    private Intent finishTransaction(ICCCard card, String uuid, MainActivity mainActivity, TransactionEntity transactionEntity,
                                     Bundle bundle, TransactionCode transactionCode, OnlineTransactionResponse onlineTransactionResponse,
                                     ActivationRepository activationRepository, BatchRepository batchRepository) {
        if (transactionCode != TransactionCode.VOID){
            transactionEntity = transactionRepository.entityCreator(card, uuid, bundle, onlineTransactionResponse, transactionCode);
            transactionEntity.setBatchNo(batchRepository.getBatchNo());
            transactionEntity.setUlGUP_SN(batchRepository.getGroupSN());
            transactionRepository.insertTransaction(transactionEntity);
            batchRepository.updateGUPSN();
        }
        else {
            transactionRepository.setVoid(transactionEntity.getUlGUP_SN(), transactionEntity.getBaDate(), transactionEntity.getSID());
        }
        return transactionRepository.prepareIntent(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, onlineTransactionResponse.getmResponseCode());
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

    public boolean isVoidListEmpty() {
        return transactionRepository.isEmptyVoid();
    }

    public boolean isTransactionListEmpty() {
        return transactionRepository.isEmpty();
    }

}