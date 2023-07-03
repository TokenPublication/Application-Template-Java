package com.example.application_template_jmvvm.ui.sale;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.repository.CardRepository;
import com.example.application_template_jmvvm.data.model.card.ICCCard;

import javax.inject.Inject;

public class CardViewModel extends ViewModel {
    private CardRepository cardRepository;
    private MutableLiveData<ICCCard> cardLiveData = new MutableLiveData<>();
    private MutableLiveData<ContentValues> qrData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCardServiceConnect = new MutableLiveData<>(false);

    @Inject
    public CardViewModel() {}

    public MutableLiveData<Boolean> getIsCardServiceConnect() {
        return isCardServiceConnect;
    }

    public void setIsCardServiceConnect(boolean isConnected) {
        isCardServiceConnect.postValue(isConnected);
    }

    public void initializeCardServiceBinding(MainActivity mainActivity) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (cardRepository == null) {
                cardRepository = new CardRepository(this, mainActivity);
            } else {
                cardRepository.cardServiceBinder(mainActivity);
            }
        }, 5);
    }

    public void readCard(int amount) {
        cardRepository.readCard(amount);
    }

    public LiveData<ICCCard> getCardLiveData() {
        return cardLiveData;
    }

    public void afterCardDataReceived(ICCCard card) {
        cardLiveData.postValue(card);
    }

    public CardRepository getCardRepository() {
        return cardRepository;
    }

    public ContentValues afterQrReceived(ContentValues values) {
        return values;
    }
}