package com.example.application_template_jmvvm.Entity;

public class ICCCard implements ICard {
    int resultCode;
    int mCardReadType;
    String mCardNumber;
    String mTrack2Data;
    String mExpireDate;
    int mTranAmount1;
    String mTrack1CustomerName;
    String CardSeqNum;
    String AC;
    String CID;
    String ATC;
    String TVR;
    String TSI;
    String AIP;
    String CVM;
    String AID2;
    String UN;
    String IAD;

    @Override
    public String getCardNumber() {
        return mCardNumber;
    }

    @Override
    public String getOwnerName() {
        return mTrack1CustomerName;
    }
}
