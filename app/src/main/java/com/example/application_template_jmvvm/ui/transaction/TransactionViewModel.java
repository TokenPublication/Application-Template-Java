package com.example.application_template_jmvvm.ui.transaction;

import android.content.ContentValues;
import android.content.Intent;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
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
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class TransactionViewModel extends ViewModel{

    private TransactionRepository transactionRepository;
    private MutableLiveData<Intent> intentLiveData  = new MutableLiveData<>();
    private MutableLiveData<TransactionResponse> transactionResponseLiveData  = new MutableLiveData<>();
    private MutableLiveData<String> showDialogLiveData = new MutableLiveData<>();

    @Inject
    public TransactionViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void prepareSlip(Fragment fragment, ActivationRepository activationRepository,
                            BatchRepository batchRepository, MainActivity mainActivity, TransactionResponse transactionResponse){
        transactionRepository.prepareSlip(this, activationRepository, batchRepository, mainActivity, fragment, transactionResponse);
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

    public void insertTransaction(TransactionEntity transaction) {
        Completable.fromAction(() -> transactionRepository.insertTransaction(transaction))
                .subscribeOn(Schedulers.io())
                .subscribe();
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