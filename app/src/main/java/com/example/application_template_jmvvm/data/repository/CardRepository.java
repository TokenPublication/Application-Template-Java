package com.example.application_template_jmvvm.data.repository;

import android.content.ContentValues;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.card.MSRCard;
import com.example.application_template_jmvvm.MainActivity;
import com.google.gson.Gson;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import javax.inject.Inject;

public class CardRepository implements CardServiceListener {

    public interface RepositoryCallback {
        void afterCardDataReceived(ICCCard card);
        void afterCardServiceConnected(Boolean isConnected);
        void setCallBackMessage(CardServiceResult cardServiceResult);
        void afterQrDataReceived(ContentValues contentValues);
    }

    private RepositoryCallback repositoryCallback;
    private ICCCard card;
    private MSRCard msrCard;
    private CardServiceBinding cardServiceBinding;
    private CardServiceListener cardServiceListener;
    private int amount;
    private boolean isGIB = false;

    @Inject
    public CardRepository() {
        this.cardServiceListener = this;
    }

    public void callbackInitializer(RepositoryCallback repositoryCallback) {
        this.repositoryCallback = repositoryCallback;
    }

    public void cardServiceBinder(MainActivity mainActivity) {
        this.cardServiceBinding = new CardServiceBinding(mainActivity, cardServiceListener);
    }

    public void setGIB(boolean GIB) {
        isGIB = GIB;
    }

    public CardServiceBinding getCardServiceBinding() {
        return cardServiceBinding;
    }

    public void readCard(int amount) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 0);
            obj.put("zeroAmount", 1);
            obj.put("showAmount", (amount == 0) ? 0 : 1);
            obj.put("fallback", 1);
            obj.put("qrPay", 1);
            obj.put("cardReadTypes",6);
            obj.put("partialEMV", 1);
            obj.put("keyIn", 1);
            obj.put("askCVV", 1);
            if (isGIB) {
                obj.put("showCardScreen", 0);
                isGIB = false;
            }
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
            int resultCode = json.getInt("resultCode");

            if (resultCode == CardServiceResult.USER_CANCELLED.resultCode()) {
                Log.d("CardDataReceived","Card Result Code: User Cancelled");
                repositoryCallback.setCallBackMessage(CardServiceResult.USER_CANCELLED);
            }

            if (resultCode == CardServiceResult.ERROR_TIMEOUT.resultCode()) {
                Log.d("CardDataReceived","Card Result Code: TIMEOUT");
                repositoryCallback.setCallBackMessage(CardServiceResult.ERROR_TIMEOUT);
            }

            if (resultCode == CardServiceResult.ERROR.resultCode()) {
                Log.d("CardDataReceived","Card Result Code: ERROR");
                repositoryCallback.setCallBackMessage(CardServiceResult.ERROR);
            }

            if (resultCode == CardServiceResult.SUCCESS.resultCode()) {
                int type = json.getInt("mCardReadType");
                if (type == CardReadType.QrPay.value) {
                    ContentValues values = new ContentValues();
                    values.put(TransactionCols.col_bCardReadType, type);
                    values.put(TransactionCols.col_ulAmount, json.getInt("mTranAmount1"));
                    repositoryCallback.afterQrDataReceived(values);
                    return;
                }
                if (type == CardReadType.CLCard.value) {
                    this.card = new Gson().fromJson(cardData, ICCCard.class);
                } else if (type == CardReadType.ICC.value) {
                    this.card = new Gson().fromJson(cardData, ICCCard.class);
                } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                    MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                    this.msrCard = card;
                    cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
                }
                cardServiceBinding.unBind();
                repositoryCallback.afterCardDataReceived(card);
            }
        } catch (Exception e) {
            repositoryCallback.setCallBackMessage(CardServiceResult.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void onCardServiceConnected() {
        repositoryCallback.afterCardServiceConnected(true);
    }

    @Override
    public void onPinReceived(String s) {}

    @Override
    public void onICCTakeOut() {}
}
