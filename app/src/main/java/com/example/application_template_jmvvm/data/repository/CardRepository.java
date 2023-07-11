package com.example.application_template_jmvvm.data.repository;

import android.content.ContentValues;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.card.MSRCard;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.google.gson.Gson;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

public class CardRepository implements CardServiceListener{

    private ICCCard card;
    private MSRCard msrCard;
    private CardServiceBinding cardServiceBinding;
    private CardServiceListener cardServiceListener;
    private int amount;
    private CardViewModel cardViewModel;

    public CardRepository(CardViewModel cardViewModel, MainActivity mainActivity) {
        this.cardViewModel = cardViewModel;
        this.cardServiceListener = this;
        this.cardServiceBinding = new CardServiceBinding(mainActivity, cardServiceListener);
    }

    public CardServiceBinding getCardServiceBinding() {
        return cardServiceBinding;
    }

    public void cardServiceBinder(MainActivity mainActivity) {
        this.cardServiceBinding = new CardServiceBinding(mainActivity, cardServiceListener);
    }

    public void readCard(int amount) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("showAmount", (amount == 0) ? 0 : 1);
            obj.put("fallback", 1);
            obj.put("cardReadTypes", 6);
            obj.put("qrPay", 1);
            this.amount = amount;
            cardServiceBinding.getCard(amount, 30, obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCardDataReceived(String cardData) {
        Log.d("Card Data", cardData);
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");        //TODO: resultCode
            if (type == CardReadType.QrPay.value) {
                ContentValues values = new ContentValues();
                values.put(TransactionCols.col_bCardReadType, type);
                values.put(TransactionCols.col_ulAmount, json.getInt("mTranAmount1"));
                cardViewModel.afterQrReceived(values);
                return;
            }
            if (type == CardReadType.CLCard.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.msrCard = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
            }
            cardViewModel.afterCardDataReceived(card);  //TODO livedata ile yukarıya ?
        } catch (Exception e) {         //TODO: handle
            e.printStackTrace();
        }
    }


    @Override
    public void onCardServiceConnected() {
        cardViewModel.setIsCardServiceConnect(true);
    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {

    }
}
