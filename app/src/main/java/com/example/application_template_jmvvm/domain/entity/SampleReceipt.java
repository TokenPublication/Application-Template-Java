package com.example.application_template_jmvvm.domain.entity;

public class SampleReceipt {
    private String merchantName;
    private String merchantID;
    private String posID;
    private String cardNo;
    private String fullName;
    private String amount;
    private String groupNo;
    private String aid;
    private String serialNo;
    private String approvalCode;

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

    public String getSerialNo() { return serialNo; }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getApprovalCode() { return approvalCode; }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}