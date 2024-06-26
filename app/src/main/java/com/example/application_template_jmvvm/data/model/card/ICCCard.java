package com.example.application_template_jmvvm.data.model.card;

import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;

/**
 * This is a class for keeping ICC card data.
 */
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
     String AIDLabel;
     String SID;
     String DateTime;
     String UN;
     String IAD;
     int OnlPINReq;

    public int getResultCode() {
        return resultCode;
    }

    public int getmCardReadType() {
        return mCardReadType;
    }

    public String getmCardNumber() {
        return mCardNumber;
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

    public String getmTrack1CustomerName() {
        return mTrack1CustomerName;
    }

    public String getCardSeqNum() {
        return CardSeqNum;
    }

    public String getAC() {
        return AC;
    }

    public String getCID() {
        return CID;
    }

    public String getATC() {
        return ATC;
    }

    public String getTVR() {
        return TVR;
    }

    public String getTSI() {
        return TSI;
    }

    public String getAIP() {
        return AIP;
    }

    public String getCVM() {
        return CVM;
    }

    public String getAID2() {
        return AID2;
    }

    public String getAIDLabel() {
        return AIDLabel;
    }

    public String getSID() {
        return SID;
    }

    public String getDateTime() {
        return DateTime;
    }

    public String getUN() {
        return UN;
    }

    public String getIAD() {
        return IAD;
    }

    @Override
    public String getCardNumber() {
        return mCardNumber;
    }

    @Override
    public String getOwnerName() {
        return mTrack1CustomerName;
    }

    public int getOnlPINReq() {
        return OnlPINReq;
    }

    public void setOnlPINReq(int onlPINReq) {
        OnlPINReq = onlPINReq;
    }

    public boolean isPinByPass() {
        if (mCardReadType == CardReadType.ICC.value) {
            byte flag = (byte) StringHelper.hexStringtoAscii(TVR).charAt(2);
            return (flag & 0x08) == (byte) 0x08 || (flag & 0x10) == (byte) 0x10 || (flag & 0x20) == (byte) 0x20;
        }
        return false;
    }
}
