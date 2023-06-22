package com.example.application_template_jmvvm.ui.transaction;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.data.model.response.TransactionResponse;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.token.uicomponents.CustomInput.CustomInputFormat;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class TransactionViewModel extends ViewModel{

    private TransactionRepository transactionRepository;
    private MutableLiveData<Intent> intentLiveData  = new MutableLiveData<>();
    private MutableLiveData<TransactionResponse> transactionResponseLiveData  = new MutableLiveData<>();
    private MutableLiveData<String> showDialogLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isButtonClickedLiveData = new MutableLiveData<>(false);
    @Inject
    public TransactionViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void TransactionRoutine(ICCCard card, String uuid, MainActivity mainActivity, Fragment fragment, ContentValues extraContentValues,
                                   Bundle bundle, TransactionCode transactionCode, ActivationRepository activationRepository, BatchRepository batchRepository){
        TransactionViewModel transactionViewModel = this;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setShowDialogLiveData("Progress");
        Observable<ICCCard> observable = Observable.just(card)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        Observer<ICCCard> observer = new Observer<ICCCard>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(ICCCard card) {
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
                OnlineTransactionResponse onlineTransactionResponse = transactionRepository.parseResponse(transactionViewModel);
                Intent resultIntent = finishTransaction(card, uuid, mainActivity, fragment, extraContentValues,
                                                        bundle, transactionCode, onlineTransactionResponse, activationRepository, batchRepository);
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

    private Intent finishTransaction(ICCCard card, String uuid, MainActivity mainActivity, Fragment fragment, ContentValues extraContentValues,
                                                  Bundle bundle, TransactionCode transactionCode, OnlineTransactionResponse onlineTransactionResponse,
                                                  ActivationRepository activationRepository, BatchRepository batchRepository){
        TransactionEntity transactionEntity = transactionRepository.entityCreator(card, uuid, extraContentValues, bundle, onlineTransactionResponse, transactionCode);
        transactionEntity.setBatchNo(batchRepository.getBatchNo());
        if (transactionCode != TransactionCode.VOID){
            transactionEntity.setUlGUP_SN(batchRepository.getGroupSN());
            transactionRepository.insertTransaction(transactionEntity);
            batchRepository.incrementGUPSN();
        }
        else {
            transactionEntity.setUlGUP_SN(Integer.parseInt(extraContentValues.get(TransactionCols.col_ulGUP_SN).toString()));
            transactionRepository.setVoid(transactionEntity.getUlGUP_SN(),transactionEntity.getBaDate(),transactionEntity.getSID());
        }
        return transactionRepository.prepareIntent(activationRepository, batchRepository, mainActivity, fragment, transactionEntity, onlineTransactionResponse.getmResponseCode());
    }

    public ContentValues prepareContents(ICCCard card, String uuid, TransactionCode transactionCode){
        return transactionRepository.prepareContentValues(card, uuid, transactionCode);
    }

    public ContentValues prepareExtraContents(ContentValues values, TransactionCode transactionCode,
                                              List<CustomInputFormat> inputList){
        return transactionRepository.putExtraContents(values, transactionCode, inputList);
    }

    public void prepareDummyResponse(ActivationRepository activationRepository, BatchRepository batchRepository, MainActivity mainActivity,
                                     Fragment fragment, Integer price, ResponseCode code, Boolean hasSlip,
                                       SlipType slipType, String cardNo, String ownerName, int paymentType){
        transactionRepository.prepareDummyResponse(this, activationRepository, batchRepository, mainActivity,
                                                    fragment, price, code, hasSlip, slipType, cardNo, ownerName, paymentType);
    }

    public MutableLiveData<Intent> getIntentLiveData() {
        return intentLiveData;
    }

    public void setIntentLiveData(Intent intent) {
        intentLiveData.postValue(intent);
    }

    public MutableLiveData<TransactionResponse> getTransactionResponseLiveData() {
        return transactionResponseLiveData;
    }

    public void setTransactionResponseLiveData(TransactionResponse transactionResponse) {
        transactionResponseLiveData.postValue(transactionResponse);
    }

    public MutableLiveData<String> getShowDialogLiveData() {
        return showDialogLiveData;
    }

    public void setShowDialogLiveData(String text) {
        showDialogLiveData.postValue(text);
    }

    public MutableLiveData<Boolean> getIsButtonClickedLiveData() {
        return isButtonClickedLiveData;
    }

    public void setIsButtonClickedLiveData(Boolean isClicked) {
         isButtonClickedLiveData.postValue(isClicked);
    }

    public List<TransactionEntity> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public List<TransactionEntity> getTransactionsByRefNo(String refNo) {
        return transactionRepository.getTransactionsByRefNo(refNo);
    }

    public List<TransactionEntity> getTransactionsByCardNo(String cardNo) {
        return transactionRepository.getTransactionsByCardNo(cardNo);
    }

    public void setVoid(int gupSN, String date, String card_SID) {
        transactionRepository.setVoid(gupSN, date, card_SID);
    }

    public boolean isTransactionListEmpty() {
        return transactionRepository.isEmpty();
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAll();
    }

}