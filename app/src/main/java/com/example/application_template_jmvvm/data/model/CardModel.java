package com.example.application_template_jmvvm.data.model;

import android.content.ContentValues;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.domain.entity.CardReadType;
import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.domain.entity.MSRCard;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
import com.example.application_template_jmvvm.ui.transaction.CardViewModel;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.google.gson.Gson;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import java.util.List;

public class CardModel implements CardServiceListener{

    private ICCCard card;
    private MSRCard msrCard;
    private CardServiceBinding cardServiceBinding;
    private CardServiceListener cardServiceListener;
    private int amount;
    private CardViewModel cardViewModel;

    public CardModel(CardViewModel cardViewModel, MainActivity mainActivity) {
        this.cardViewModel = cardViewModel;
        this.cardServiceListener = this;
        this.cardServiceBinding = new CardServiceBinding(mainActivity, cardServiceListener);
    }

    public void readCard(int amount) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes", 6);
            obj.put("qrPay", 1);
            this.amount = amount;
            cardServiceBinding.getCard(amount, 40, obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCardDataReceived(String cardData) {
        Log.d("Card Data", cardData);
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");
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
            cardViewModel.afterCardDataReceived(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ContentValues prepareContentValues(ICCCard card, String uuid) {
        ContentValues values = new ContentValues();
        values.put(TransactionCol.col_uuid.name(), uuid);
        values.put(TransactionCol.col_ulSTN.name(), "STN");
        values.put(TransactionCol.col_bCardReadType.name(), card.getmCardReadType());
        values.put(TransactionCol.col_bTransCode.name(), 55);
        values.put(TransactionCol.col_ulAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baPAN.name(), card.getmCardNumber());
        values.put(TransactionCol.col_baExpDate.name(), card.getmExpireDate());
        values.put(TransactionCol.col_baDate.name(), card.getDateTime().substring(0, 8));
        values.put(TransactionCol.col_baTime.name(), card.getDateTime().substring(8));
        values.put(TransactionCol.col_baTrack2.name(), card.getmTrack2Data());
        values.put(TransactionCol.col_baCustomName.name(), card.getmTrack1CustomerName());
        values.put(TransactionCol.col_baRspCode.name(), 3);
        values.put(TransactionCol.col_baTranDate.name(), card.getDateTime());
        values.put(TransactionCol.col_baHostLogKey.name(), "1020304050");
        values.put(TransactionCol.col_authCode.name(), "10203040");
        values.put(TransactionCol.col_aid.name(), card.getAID2());
        values.put(TransactionCol.col_aidLabel.name(), card.getAIDLabel());
        values.put(TransactionCol.col_baCVM.name(), card.getCVM());
        values.put(TransactionCol.col_SID.name(), card.getSID());
        return values;
    }

    public ContentValues putExtraContents(ContentValues values, TransactionCode transactionCode,
                                          List<CustomInputFormat> inputList) {
        switch (transactionCode) {
            case MATCHED_REFUND:
                values.put(TransactionCol.col_ulAmount.name(),Integer.parseInt(inputList.get(0).getText()));
                values.put(TransactionCol.col_ulAmount2.name(), Integer.parseInt(inputList.get(1).getText()));
                values.put(TransactionCol.col_authCode.name(), inputList.get(3).getText());
                values.put(TransactionCol.col_baTranDate2.name(), inputList.get(4).getText());
                break;
            case CASH_REFUND:
                values.put(TransactionCol.col_ulAmount2.name(), Integer.parseInt(inputList.get(0).getText()));
                break;
            case INSTALLMENT_REFUND:
                // Handle installment refund type
                break;
            default:
                // Default
                break;
        }
        return values;
    }

    @Override
    public void onCardServiceConnected() {

    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {

    }
}
