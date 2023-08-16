package com.example.application_template_jmvvm.utils.printHelpers;

public class StringHelper {

    private final static int d_EMVCL_SID_VISA_OLD_US       = 0x13;
    private final static int d_EMVCL_SID_VISA_WAVE_2      = 0x16;
    private final static int d_EMVCL_SID_VISA_WAVE_QVSDC   = 0x17;
    private final static int d_EMVCL_SID_VISA_WAVE_MSD = 0x18;
    private final static int d_EMVCL_SID_PAYPASS_MAG_STRIPE= 0x20;
    private final static int d_EMVCL_SID_PAYPASS_MCHIP = 0x21;
    private final static int d_EMVCL_SID_JCB_WAVE_2       = 0x61;
    private final static int d_EMVCL_SID_JCB_WAVE_QVSDC    = 0x62;
    private final static int d_EMVCL_SID_JCB_EMV         = 0x63;
    private final static int d_EMVCL_SID_JCB_MSD         = 0x64;
    private final static int d_EMVCL_SID_JCB_LEGACY       = 0x65;
    private final static int d_EMVCL_SID_AE_EMV          = 0x50;
    private final static int d_EMVCL_SID_AE_MAG_STRIPE = 0x52;
    private final static int d_EMVCL_SID_DISCOVER        = 0x41;
    private final static int d_EMVCL_SID_DISCOVER_DPAS = 0x42;
    private final static int d_EMVCL_SID_INTERAC_FLASH = 0x48;
    private final static int d_EMVCL_SID_MEPS_MCCS    = 0x81;
    private final static int d_EMVCL_SID_CUP_QPBOC    = 0x91;

    public static String getAmount(int amount) {
        String currency;

        currency = "₺";

        String str=String.valueOf(amount);
        if (str.length() == 1) str = "00" + str;
        else if (str.length() == 2) str = "0" + str;

        String s1=str.substring(0,str.length()-2);
        String s2=str.substring(str.length()-2);
        return s1 + "," + s2 + currency;
    }

    public static String getInstAmount(int amount) {
        String str=String.valueOf(amount);
        if (str.length() == 1) str = "00" + str;
        else if (str.length() == 2) str = "0" + str;

        String s1=str.substring(0,str.length()-2);
        String s2=str.substring(str.length()-2);
        return s1 + "," + s2;
    }

    public static String GenerateApprovalCode(String BatchNo, String TransactionNo, String SaleID) {
        String approvalCode;
        approvalCode = BatchNo + TransactionNo + SaleID;
        return approvalCode;
    }


    public static String maskCardNumber(String cardNo) {
        // 1234 **** **** 7890
        String prefix = cardNo.substring(0, 4);
        String suffix = cardNo.substring(cardNo.length() - 4);
        StringBuilder masked = new StringBuilder(prefix);
        for (int i = 4; i < cardNo.length() - 4; i++) {
            masked.append("*");
        }
        masked.append(suffix);
        StringBuilder formatted = new StringBuilder(masked);
        int index = 4;
        while (index < formatted.length()) {
            formatted.insert(index, " ");
            index += 5;
        }
        return formatted.toString();
    }


    public static String MaskTheCardNo(String cardNo){
        // CREATE A MASKED CARD NO
        // First 6 and Last 4 digit is visible, others are masked with '*' Card No can be 16,17,18 Digits...
        // 123456******0987
        String CardNoFirstSix = cardNo.substring(0, 6);
        String CardNoLastFour =  cardNo.substring(cardNo.length() - 4);
        StringBuilder masked = new StringBuilder(CardNoFirstSix);
        for (int i = 4; i < cardNo.length() - 4; i++) {
            masked.append("*");
        }
        masked.append(CardNoLastFour);
        StringBuilder formatted = new StringBuilder(masked);

        return formatted.toString();
    }

    public static String hexStringtoAscii(String hexString){

        StringBuilder output = new StringBuilder();

        for(int i = 0; i < hexString.length(); i+=2) {

            String str2 = hexString.substring(i, i+2);
            output.append((char)Integer.parseInt(str2, 16));

        }
        return output.toString();
    }

    protected static String[] getContactlessLabel(int sid) {
        String[] label = new String[2];

        switch(sid)
        {
            case d_EMVCL_SID_PAYPASS_MAG_STRIPE:
                label[0] = " A ";
                label[1] = "MASTERCARD";
                break;
            case d_EMVCL_SID_PAYPASS_MCHIP:
                label[0] = " M ";
                label[1] = "MASTERCARD";
                break;
            case d_EMVCL_SID_VISA_WAVE_QVSDC:
                label[0] = " Q ";
                label[1] = "VISA";
                break;
            case d_EMVCL_SID_VISA_WAVE_MSD:
                label[0] = " D ";
                label[1] = "VISA";
                break;
            case 66/*TROY*/:
                label[0] = " T ";
                label[1] = "TROY";
                break;
            case d_EMVCL_SID_CUP_QPBOC:
                label[0] = " U ";
                label[1] = "CUP";
                break;
            default:
                label[0] = " D ";
                label[1] = "";
                break;
        }
        return label;
    }
}
