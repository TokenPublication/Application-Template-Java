package com.example.application_template_jmvvm.data.model.response;

import com.example.application_template_jmvvm.data.model.code.ResponseCode;

public class OnlineTransactionResponse {
    ResponseCode mResponseCode;
    String mTextPrintCode1;
    String mTextPrintCode2;
    String mAuthCode;
    String mRefNo;
    String mDisplayData;
    String mKeySequenceNumber;
    Integer insCount;
    String dateTime;

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

    public String getmRefNo() {
        return mRefNo;
    }

    public void setmRefNo(String mRefNo) {
        this.mRefNo = mRefNo;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


}
