package com.example.application_template_jmvvm.ui.sale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.repository.CardRepository;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.tokeninc.cardservicebinding.CardServiceBinding;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * This viewModel contains LiveData variables and cardRepository for card operations.
 * LiveData variables can be observable in UI elements (fragments, activity). This flow
 * comes from MVVM architectural design. Also it is callback listener for RepositoryCallback
 * interface in CardRepository
 */

@HiltViewModel
public class CardViewModel extends ViewModel implements CardRepository.RepositoryCallback {
    private CardRepository cardRepository;
    private MutableLiveData<ICCCard> cardLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCardServiceConnect = new MutableLiveData<>(false);
    private MutableLiveData<ResponseCode> responseMessageLiveData = new MutableLiveData<>();

    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    @Inject
    public CardViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        cardRepository.callbackInitializer(this);
    }

    public void initializeCardServiceBinding(MainActivity mainActivity) {
        cardRepository.cardServiceBinder(mainActivity);
    }

    public void setEMVConfiguration() {
        cardRepository.setEMVConfiguration();
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
    public void setResponseMessage(ResponseCode responseCode) {
        responseMessageLiveData.postValue(responseCode);
    }

    public MutableLiveData<ResponseCode> getResponseMessageLiveData() {
        return responseMessageLiveData;
    }

    @Override
    public void setMessage(String message) {
        messageLiveData.postValue(message);
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
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
