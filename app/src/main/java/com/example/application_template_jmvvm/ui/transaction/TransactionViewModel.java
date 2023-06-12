package com.example.application_template_jmvvm.ui.transaction;

import android.content.ContentValues;
import android.content.Context;

import javax.inject.Inject;
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
    private MutableLiveData<TransactionEntity> insertedTransaction = new MutableLiveData<>();
    private MutableLiveData<TransactionResponse> transactionResponseLiveData = new MutableLiveData<>();

    @Inject
    public TransactionViewModel() {}

    public void setter(MainActivity main) {
        this.main = main;
        transactionDao = TransactionDatabase.getDatabase(main.getApplication()).transactionDao();
        transactionRepository = new TransactionRepository(transactionDao);
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

    public LiveData<TransactionResponse> getTransactionResponseLiveData() {
        return transactionResponseLiveData;
    }

    public LiveData<TransactionEntity> getInsertedTransaction() {
        return insertedTransaction;
    }

    public void insertTransaction(TransactionEntity transaction) {
        transactionRepository.insertTransaction(transaction);       //TODO IOda yapılcak
        insertedTransaction.postValue(transaction);
    }

    //TODO View state

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

    public void performSaleTransaction(ICCCard card, TransactionService transactionService, Context context, String uuid) {
        ContentValues values = cardModel.prepareContentValues(card, uuid);
        final TransactionResponse[] transactionResponse = {new TransactionResponse()};
        transactionService.doInBackground(main, context, values,TransactionCode.SALE, this,
                new TransactionResponseListener() {
            @Override
            public void onComplete(TransactionResponse response) {
                transactionResponse[0] = response;
                transactionResponseLiveData.postValue(transactionResponse[0]);
            }
        });
    }

    public void performRefundTransaction(ICCCard card, TransactionCode transactionCode,
                                         TransactionService transactionService, Context context, String uuid,
                                            List<CustomInputFormat> inputList) {
        ContentValues values = cardModel.prepareContentValues(card, uuid);
        values = cardModel.putExtraContents(values,transactionCode,inputList);
        final TransactionResponse[] transactionResponse = {new TransactionResponse()};
        transactionService.doInBackground(main, context, values, transactionCode,this,
                new TransactionResponseListener() {
            @Override
            public void onComplete(TransactionResponse response) {
                transactionResponse[0] = response;
                transactionResponseLiveData.postValue(transactionResponse[0]);
            }
        });
    }
}