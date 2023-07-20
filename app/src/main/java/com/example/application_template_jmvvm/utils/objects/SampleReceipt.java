package com.example.application_template_jmvvm.utils.objects;

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
    private String serialNo;
    private String approvalCode;

    public SampleReceipt (String cardNo, String ownerName, int amount, ActivationRepository activationRepository, BatchRepository batchRepository) {
        setMerchantName("TOKEN FINTECH");
        setMerchantID(activationRepository.getMerchantId());
        setPosID(activationRepository.getTerminalId());
        setCardNo(StringHelper.maskCardNumber(cardNo));
        setFullName(ownerName);
        setAmount(StringHelper.getAmount(amount));
        setGroupNo(String.valueOf(batchRepository.getBatchNo()));
        setAid("A0000000000031010");
        setSerialNo(String.valueOf(batchRepository.getGroupSN()));
        setApprovalCode(StringHelper.GenerateApprovalCode(String.valueOf(batchRepository.getBatchNo()), String.valueOf(batchRepository.getGroupSN()), String.valueOf(batchRepository.getGroupSN()-1)));
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

    public String getSerialNo() { return serialNo; }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getApprovalCode() { return approvalCode; }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}
