package com.example.application_template_jmvvm.utils.objects;

import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;

public class SampleReceipt {
    private String merchantName;
    private String merchantID;
    private String posID;
    private String cardNo;
    private String fullName;
    private String amount;
    private String groupNo;
    private String aid;
    private String aidLabel;
    private String authCode;
    private String refNo;
    private String serialNo;
    private int isOffline;

    public SampleReceipt (Transaction transaction, ActivationRepository activationRepository, BatchRepository batchRepository,
                        OnlineTransactionResponse onlineTransactionResponse) {
        setMerchantName("TOKEN FINTECH");
        setMerchantID(activationRepository.getMerchantId());
        setPosID(activationRepository.getTerminalId());
        setCardNo(StringHelper.maskCardNumber(transaction.getBaPAN()));
        setFullName(transaction.getBaCustomerName());
        setAmount(StringHelper.getAmount(transaction.getUlAmount()));
        setGroupNo(String.valueOf(batchRepository.getBatchNo()));
        setAid(transaction.getAid());
        setAidLabel(transaction.getAidLabel());
        if (onlineTransactionResponse != null) {
            setAuthCode(onlineTransactionResponse.getmAuthCode());
            setRefNo(onlineTransactionResponse.getmRefNo());
        } else {
            setAuthCode(transaction.getAuthCode());
            setRefNo(transaction.getRefNo());
        }
        setSerialNo(String.valueOf(transaction.getUlGUP_SN()));
        setIsOffline(transaction.getIsOffline());
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getPosID() {
        return posID;
    }

    public void setPosID(String posID) {
        this.posID = posID;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) { this.groupNo = groupNo; }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getAidLabel() {
        return aidLabel;
    }

    public void setAidLabel(String aidLabel) {
        this.aidLabel = aidLabel;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getSerialNo() { return serialNo; }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }
}
