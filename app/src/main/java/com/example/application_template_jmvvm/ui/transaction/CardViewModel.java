package com.example.application_template_jmvvm.ui.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.model.CardModel;
import com.example.application_template_jmvvm.domain.entity.ICCCard;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CardViewModel extends ViewModel{
    private CardModel cardModel;
    private Boolean isCardServiceConnected = false;
    private MutableLiveData<ICCCard> cardLiveData = new MutableLiveData<>();

    @Inject
    public CardViewModel() {}

    public void initializeCardServiceBinding(MainActivity main) {
        cardModel = new CardModel(this, main);
    }

    public Boolean getIsCardServiceConnected() {
        return isCardServiceConnected;
    }

    public void setIsCardServiceConnected(boolean isConnected) {
        isCardServiceConnected = isConnected;
    }

    public void readCard(int amount) {
        cardModel.readCard(amount);
    }

    public LiveData<ICCCard> getCardLiveData() {
        return cardLiveData;
    }

    public void afterCardDataReceived(ICCCard card) {
        cardLiveData.setValue(card);
    }

    public CardModel getCardModel() {
        return cardModel;
    }

}