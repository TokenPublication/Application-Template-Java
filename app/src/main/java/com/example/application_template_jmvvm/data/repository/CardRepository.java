package com.example.application_template_jmvvm.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.application_template_jmvvm.R;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        void setResponseMessage(ResponseCode responseCode);
        void setMessage(String message);
    }

    private MainActivity mainActivity;
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
        this.mainActivity = mainActivity;
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
                repositoryCallback.setResponseMessage(ResponseCode.CANCELLED);
            }

            if (resultCode == CardServiceResult.ERROR_TIMEOUT.resultCode()) {
                Log.d("CardDataReceived","Card Result Code: TIMEOUT");
                repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            }

            if (resultCode == CardServiceResult.ERROR.resultCode()) {
                Log.d("CardDataReceived","Card Result Code: ERROR");
                repositoryCallback.setResponseMessage(ResponseCode.ERROR);
            }

            if (resultCode == CardServiceResult.SUCCESS.resultCode()) {
                int type = json.getInt("mCardReadType");
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                if (type == CardReadType.ICC.value) {
                    if (!isApprove && transactionCode == TransactionCode.SALE) {
                        isApprove = true;
                    }
                }
                repositoryCallback.afterCardDataReceived(card);
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
        setEMVConfiguration();
        repositoryCallback.afterCardServiceConnected(true);
    }

    /**
     * This function only works in installation, it calls setConfig and setCLConfig
     * It also called from onCardServiceConnected method of Card Service Library, if Configs couldn't set in first_run
     * (it is checked from sharedPreferences), again it setConfigurations, else do nothing.
     */
    public void setEMVConfiguration() {
        SharedPreferences sharedPreference = mainActivity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        boolean firstTimeBoolean = sharedPreference.getBoolean("FIRST_RUN", false);

        if (!firstTimeBoolean) {
            setConfig();
            setCLConfig();
            editor.putBoolean("FIRST_RUN", true);
            Log.d("setEMVConfiguration", "ok");
            editor.apply();
        }
    }

    /**
     * It sets custom_emv_config.xml with setEMVConfiguration method in card service
     */
    public void setConfig() {
        try {
            InputStream xmlStream = mainActivity.getApplicationContext().getAssets().open("custom_emv_config.xml");
            BufferedReader r = new BufferedReader(new InputStreamReader(xmlStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                total.append(line).append('\n');
            }
            int setConfigResult = getCardServiceBinding().setEMVConfiguration(total.toString());
            repositoryCallback.setMessage("setEMVConfiguration res=" + setConfigResult);
            Log.d("emv_config", "setEMVConfiguration: " + setConfigResult);
        } catch (Exception e) {
            mainActivity.responseMessage(ResponseCode.ERROR, "EMV Configuration Error");
            e.printStackTrace();
        }
    }

    /**
     * It sets custom_emv_cl_config.xml with setEMVCLConfiguration method in card service
     */
    public void setCLConfig() {
        try {
            InputStream xmlCLStream = mainActivity.getApplicationContext().getAssets().open("custom_emv_cl_config.xml");
            BufferedReader rCL = new BufferedReader(new InputStreamReader(xmlCLStream));
            StringBuilder totalCL = new StringBuilder();
            for (String line; (line = rCL.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                totalCL.append(line).append('\n');
            }
            int setCLConfigResult = getCardServiceBinding().setEMVCLConfiguration(totalCL.toString());
            repositoryCallback.setMessage("setEMVCLConfiguration res=" + setCLConfigResult);
            Log.d("emv_config", "setEMVCLConfiguration: " + setCLConfigResult);
        } catch (Exception e) {
            mainActivity.responseMessage(ResponseCode.ERROR, "EMV CL Configuration Error");
            e.printStackTrace();
        }
    }

    @Override
    public void onPinReceived(String s) { }

    @Override
    public void onICCTakeOut() { }
}
