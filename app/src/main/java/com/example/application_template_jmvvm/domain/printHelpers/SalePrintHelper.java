package com.example.application_template_jmvvm.domain.printHelpers;

import android.content.Context;

import com.example.application_template_jmvvm.AppTemp;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.domain.SampleReceipt;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.token.printerlib.PrinterDefinitions;
import com.token.printerlib.PrinterDefinitions.Alignment;
import com.token.printerlib.StyledString;
import com.tokeninc.deviceinfo.DeviceInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalePrintHelper extends BasePrintHelper{

    public String getFormattedText(SampleReceipt receipt, TransactionEntity transactionEntity, SlipType slipType, Context context, Integer ZNO, Integer ReceiptNo)
    {
        StyledString styledText = new StyledString();
        if(slipType == SlipType.CARDHOLDER_SLIP){
            if(!((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())  && !((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())){
                printSlipHeader(styledText, receipt);
            }
        }
        else {
            printSlipHeader(styledText, receipt);
        }

        styledText.newLine();
        if (slipType == SlipType.CARDHOLDER_SLIP) {
            styledText.addTextToLine("MÜŞTERİ NÜSHASI", Alignment.Center);
            styledText.newLine();
        }
        else if (slipType == SlipType.MERCHANT_SLIP) {
            styledText.addTextToLine("İŞYERİ NÜSHASI", Alignment.Center);
            styledText.newLine();
        }
        styledText.addTextToLine("SATIŞ", Alignment.Center);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault());
        String time = sdf.format(Calendar.getInstance().getTime());

        styledText.newLine();

        if(slipType == SlipType.CARDHOLDER_SLIP){
            if(((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())|| ((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())){
                styledText.addTextToLine("C ONLINE", Alignment.Center);
            }
            else{
                styledText.addTextToLine(time + " " + "C ONLINE", Alignment.Center);
            }
        }
        else if(slipType == SlipType.MERCHANT_SLIP){
            styledText.addTextToLine(time + " " + "C ONLINE", Alignment.Center);
        }

        styledText.newLine();
        styledText.addTextToLine(receipt.getCardNo(), Alignment.Center);

        styledText.newLine();
        styledText.addTextToLine(receipt.getFullName(), Alignment.Center);

        styledText.setLineSpacing(1f);
        styledText.setFontSize(14);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);
        styledText.newLine();
        styledText.addTextToLine("TUTAR:");
        styledText.addTextToLine(receipt.getAmount(), Alignment.Right);

        styledText.setLineSpacing(0.5f);
        styledText.setFontSize(10);
        styledText.newLine();
        if (slipType == SlipType.CARDHOLDER_SLIP) {
            styledText.addTextToLine("KARŞILIĞI MAL/HİZM ALDIM", Alignment.Center);
        }
        else {
            styledText.addTextToLine("İşlem Şifre Girilerek Yapılmıştır", Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("İMZAYA GEREK YOKTUR", Alignment.Center);
        }

        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Bold);
        styledText.setFontSize(12);
        styledText.newLine();
        if (transactionEntity != null) {
            styledText.addTextToLine("SN: " + transactionEntity.getUlGUP_SN());
            styledText.addTextToLine("ONAY KODU: " + transactionEntity.getAuthCode(), Alignment.Right);
        } else {
            styledText.addTextToLine("SN: " + receipt.getSerialNo());
            styledText.addTextToLine("ONAY KODU: " + ((int) (Math.random() * 90000) + 10000), Alignment.Right);
        }

        styledText.setFontSize(8);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);
        styledText.newLine();

        if (transactionEntity != null) {
            styledText.addTextToLine("GRUP NO:" + transactionEntity.getBatchNo());
            styledText.addTextToLine("REF NO: " + transactionEntity.getBaHostLogKey(), PrinterDefinitions.Alignment.Right);
            styledText.newLine();
            styledText.addTextToLine("AID: " + transactionEntity.getAid());
            styledText.addTextToLine(transactionEntity.getAidLabel(), PrinterDefinitions.Alignment.Right);
        }
        else {
            styledText.addTextToLine("GRUP NO:" + 3);
            styledText.addTextToLine("REF NO: " + ((int) (Math.random() * 90000000) + 10000000), Alignment.Right);
            styledText.newLine();
            styledText.addTextToLine("AID: " + receipt.getAid());
            styledText.addTextToLine("54354353asd34234234", PrinterDefinitions.Alignment.Right);
        }

        if (slipType == SlipType.MERCHANT_SLIP) {
            addTextToNewLine(styledText, "*MALİ DEĞERİ YOKTUR*", Alignment.Center, 8);
        }

        if (slipType == SlipType.MERCHANT_SLIP) {
            if(((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())) {
                styledText.newLine();
                styledText.addTextToLine("Z NO: " +ZNO, Alignment.Right);
                styledText.addTextToLine("FİŞ NO: " +ReceiptNo, Alignment.Left);
            }
        }

        if (slipType == SlipType.MERCHANT_SLIP) {
            if(((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())
                    || ((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())) {

                addTextToNewLine(styledText, ((AppTemp) context.getApplicationContext()).getCurrentFiscalID(), Alignment.Center, 8);

            }
        }

        styledText.newLine();
        styledText.addTextToLine("BU İŞLEM YURT İÇİ KARTLA YAPILMIŞTIR", Alignment.Center);
        styledText.newLine();
        styledText.addTextToLine("BU BELGEYİ SAKLAYINIZ", Alignment.Center);
        styledText.newLine();

        styledText.printLogo(context);
        styledText.addSpace(100);
        return styledText.toString();
    }

}
