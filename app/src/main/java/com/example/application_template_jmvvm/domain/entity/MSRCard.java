package com.example.application_template_jmvvm.domain.entity;

public class MSRCard implements ICard {
    private int resultCode;
    private int mCardReadType;
    private String mCardNumber;
    private String mTrack2Data;
    private String mExpireDate;
    private int mTranAmount1;
    private String mTrack1CustomerName;

    public int getResultCode() {
        return resultCode;
    }

    public int getmCardReadType() {
        return mCardReadType;
    }

    @Override
    public String getCardNumber() {
        return mCardNumber;
    }

    @Override
    public String getOwnerName() {
        return mTrack1CustomerName;
    }

    public String getmTrack2Data() {
        return mTrack2Data;
    }

    public String getmExpireDate() {
        return mExpireDate;
    }

    public int getmTranAmount1() {
        return mTranAmount1;
    }
}

