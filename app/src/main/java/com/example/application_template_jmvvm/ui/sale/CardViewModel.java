package com.example.application_template_jmvvm.ui.sale;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.repository.CardRepository;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.tokeninc.cardservicebinding.CardServiceBinding;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CardViewModel extends ViewModel implements CardRepository.RepositoryCallback {
    private CardRepository cardRepository;
    private MutableLiveData<ICCCard> cardLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCardServiceConnect = new MutableLiveData<>(false);
    private MutableLiveData<CardServiceResult> cardServiceResultLiveData = new MutableLiveData<>();

    @Inject
    public CardViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        cardRepository.callbackInitializer(this);
    }

    public void initializeCardServiceBinding(MainActivity mainActivity) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> cardRepository.cardServiceBinder(mainActivity), 5);
    }
    public void readCard(int amount, TransactionCode transactionCode) {
        cardRepository.readCard(amount, transactionCode);
    }

    @Override
    public void afterCardServiceConnected(Boolean isConnected) {
        isCardServiceConnect.postValue(isConnected);
    }

    public MutableLiveData<Boolean> getIsCardServiceConnect() {
        return isCardServiceConnect;
    }

    @Override
    public void setCallBackMessage(CardServiceResult cardServiceResult) {
        cardServiceResultLiveData.postValue(cardServiceResult);
    }

    public MutableLiveData<CardServiceResult> getCardServiceResultLiveData() {
        return cardServiceResultLiveData;
    }

    @Override
    public void afterCardDataReceived(ICCCard card) {
        cardLiveData.postValue(card);
    }

    public LiveData<ICCCard> getCardLiveData() {
        return cardLiveData;
    }

    public void setCardLiveData(ICCCard card) {
        cardLiveData.postValue(card);
    }

    public CardServiceBinding getCardServiceBinding() {
        return cardRepository.getCardServiceBinding();
    }

    public void setGIB(boolean GIB) {
        cardRepository.setGIB(GIB);
    }

}