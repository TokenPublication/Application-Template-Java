package com.example.application_template_jmvvm.data.repository;

import android.util.Log;

import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.model.type.EmvProcessType;
import com.google.gson.Gson;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * This class is for managing data related card operations
 * It implements CardServiceListener interface's methods which are card service binding lib's methods.
 * It implements RepositoryCallback interface for communicate with CardViewModel
 */
public class CardRepository implements CardServiceListener {

    public interface RepositoryCallback {
        void afterCardDataReceived(ICCCard card);
        void afterCardServiceConnected(Boolean isConnected);
        void setCallBackMessage(CardServiceResult cardServiceResult);
        void setResponseMessage(ResponseCode responseCode);
    }

    private RepositoryCallback repositoryCallback;
    private CardServiceBinding cardServiceBinding;
    private CardServiceListener cardServiceListener;
    private int amount;
    private TransactionCode transactionCode;
    private boolean isGIB = false;
    private boolean isApprove = false;

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

    /**
     * This reads the card and shows card read screen with amount.
     */
    public void readCard(int amount, TransactionCode transactionCode) {
        try {
            if (!isApprove) {
                JSONObject obj = new JSONObject();
                obj.put("forceOnline", 1);
                obj.put("zeroAmount", 0);
                obj.put("reqEMVData", "575A5F245F204F84959F12");
                if (transactionCode != TransactionCode.SALE && transactionCode != TransactionCode.VOID) {
                    obj.put("emvProcessType", EmvProcessType.FULL_EMV.ordinal());
                } else {
                    obj.put("emvProcessType", EmvProcessType.READ_CARD.ordinal());
                }
                obj.put("showAmount", (transactionCode == TransactionCode.VOID) ? 0 : 1);
                obj.put("cardReadTypes",6);
                if (isGIB) {
                    obj.put("showCardScreen", 0);
                }
                this.amount = amount;
                this.transactionCode = transactionCode;
                getCard(amount, obj.toString());
            } else {
                approveCard();
            }
        } catch (Exception e) {
            repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * This method is triggered after reading card, if card couldn't be read successfully a callback message is arranged
     * If card read successfully, it parses @param cardData and creates ICCCard object. Finally, communicate with CardViewModel
     * for set cardLiveData to card object that we created before related to cardData.
     */
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
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                if (type == CardReadType.QrPay.value) {
                    repositoryCallback.afterCardDataReceived(card);
                }
                if (type == CardReadType.CLCard.value) {
                    repositoryCallback.afterCardDataReceived(card);
                } else if (type == CardReadType.ICC.value) {
                    if (!isApprove && transactionCode == TransactionCode.SALE) {
                        isApprove = true;
                    }
                    repositoryCallback.afterCardDataReceived(card);
                } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                    String cardNo = json.getString("mCardNumber");
                    cardServiceBinding.getOnlinePIN(amount, cardNo, 0x0A01, 0, 4, 8, 30);
                }
            }
        } catch (Exception e) {
            repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * This method for read card with Continue_Emv.
     */
    private void approveCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("showAmount", 1);
            obj.put("emvProcessType", EmvProcessType.CONTINUE_EMV.ordinal());
            getCard(amount, obj.toString());
        } catch (Exception e) {
            repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * This method is putting some arguments to JSONObject for card read and calls getCard() method in cardService.
     */
    private void getCard(int amount, String config) {
        try {
            JSONObject obj = new JSONObject(config);
            //TODO Developer, check from parameter
            boolean isKeyInAllowed = true;
            boolean isAskCVVAllowed = true;
            boolean isFallbackAllowed = true;
            boolean isQrPayAllowed = true;
            obj.put("keyIn", isKeyInAllowed ? 1 : 0);
            obj.put("askCVV", isAskCVVAllowed ? 1 : 0);
            obj.put("fallback", isFallbackAllowed ? 1 : 0);
            obj.put("qrPay", isQrPayAllowed ? 1 : 0);
            cardServiceBinding.getCard(amount, 30, obj.toString());
        } catch (Exception e) {
            repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * If card service connect successful, this function will be triggered and communicate with CardViewModel
     * for set cardServiceResultLiveData to true.
     */
    @Override
    public void onCardServiceConnected() {
        repositoryCallback.afterCardServiceConnected(true);
    }

    @Override
    public void onPinReceived(String s) { }

    @Override
    public void onICCTakeOut() { }
}
