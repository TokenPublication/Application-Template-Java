package com.example.application_template_jmvvm.data.response;

import com.example.application_template_jmvvm.domain.entity.ResponseCode;

public class OnlineTransactionResponse {
    ResponseCode mResponseCode = null;
    String mTextPrintCode1 = null;
    String mTextPrintCode2 = null;
    String mAuthCode = null;
    String mHostLogKey = null;
    String mDisplayData = null;
    String mKeySequenceNumber = null;
    Integer insCount = null;
    Integer instAmount = null;
    String dateTime = null;

    public ResponseCode getmResponseCode() {
        return mResponseCode;
    }

    public void setmResponseCode(ResponseCode mResponseCode) {
        this.mResponseCode = mResponseCode;
    }

    public String getmTextPrintCode1() {
        return mTextPrintCode1;
    }

    public void setmTextPrintCode1(String mTextPrintCode1) {
        this.mTextPrintCode1 = mTextPrintCode1;
    }

    public String getmTextPrintCode2() {
        return mTextPrintCode2;
    }

    public void setmTextPrintCode2(String mTextPrintCode2) {
        this.mTextPrintCode2 = mTextPrintCode2;
    }

    public String getmAuthCode() {
        return mAuthCode;
    }

    public void setmAuthCode(String mAuthCode) {
        this.mAuthCode = mAuthCode;
    }

    public String getmHostLogKey() {
        return mHostLogKey;
    }

    public void setmHostLogKey(String mHostLogKey) {
        this.mHostLogKey = mHostLogKey;
    }

    public String getmDisplayData() {
        return mDisplayData;
    }

    public void setmDisplayData(String mDisplayData) {
        this.mDisplayData = mDisplayData;
    }

    public String getmKeySequenceNumber() {
        return mKeySequenceNumber;
    }

    public void setmKeySequenceNumber(String mKeySequenceNumber) {
        this.mKeySequenceNumber = mKeySequenceNumber;
    }

    public Integer getInsCount() {
        return insCount;
    }

    public void setInsCount(Integer insCount) {
        this.insCount = insCount;
    }

    public Integer getInstAmount() {
        return instAmount;
    }

    public void setInstAmount(Integer instAmount) {
        this.instAmount = instAmount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


}
