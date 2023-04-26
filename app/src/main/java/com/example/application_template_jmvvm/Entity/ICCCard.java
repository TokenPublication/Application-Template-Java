package com.example.application_template_jmvvm.Entity;

public class ICCCard implements ICard {
    public int resultCode;
    public int mCardReadType;
    public String mCardNumber;
    public String mTrack2Data;
    public String mExpireDate;
    public int mTranAmount1;
    String mTrack1CustomerName;
    public String CardSeqNum;
    public String AC;
    public String CID;
    public String ATC;
    public String TVR;
    public String TSI;
    public String AIP;
    public String CVM;
    public String AID2;
    public String AIDLabel;
    public String SID;
    public String DateTime;
    public String UN;
    public String IAD;

    @Override
    public String getCardNumber() {
        return mCardNumber;
    }

    @Override
    public String getOwnerName() {
        return mTrack1CustomerName;
    }
}
