package com.example.application_template_jmvvm.ui.transaction;

import android.content.ContentValues;
import android.content.Context;

import javax.inject.Inject;

import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.repository.BatchRepository;
import com.example.application_template_jmvvm.data.model.CardModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.data.database.TransactionDatabase;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.database.repository.TransactionRepository;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.data.service.TransactionResponseListener;
import com.example.application_template_jmvvm.data.service.TransactionService;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.token.uicomponents.CustomInput.CustomInputFormat;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TransactionViewModel extends ViewModel{
    private MainActivity main;
    private TransactionRepository transactionRepository;
    private TransactionDao transactionDao;
    private CardModel cardModel;
    private MutableLiveData<Boolean> isCardServiceConnected = new MutableLiveData<>(false);
    private MutableLiveData<ICCCard> cardLiveData = new MutableLiveData<>();

    @Inject
    public TransactionViewModel(MainActivity main, TransactionRepository transactionRepository) {
        this.main = main;
        this.transactionRepository = transactionRepository;
    }

    public void initializeCardServiceBinding() {
        cardModel = new CardModel(this,main);
    }

    public LiveData<Boolean> getIsCardServiceConnected() {
        return isCardServiceConnected;
    }

    public void setIsCardServiceConnected(boolean isConnected) {
        isCardServiceConnected.setValue(isConnected);
    }

    public LiveData<ICCCard> getCardLiveData() {
        return cardLiveData;
    }

    private void setCard(ICCCard card) {
        cardLiveData.setValue(card);
    }

    public void readCard(int amount) {
        if (cardModel != null) {
            cardModel.readCard(amount);
        }
    }

    public void afterCardDataReceived(ICCCard card) {
        setCard(card);
    }

    public CardModel getCardModel() {
        return cardModel;
    }

    public void insertTransaction(TransactionEntity transaction) {
        transactionRepository.insertTransaction(transaction);       //TODO IOda yapılcak
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