package com.example.application_template_jmvvm.utils.printHelpers;

import android.content.Context;

import com.example.application_template_jmvvm.AppTemp;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.token.printerlib.PrinterDefinitions;
import com.token.printerlib.PrinterDefinitions.Alignment;
import com.token.printerlib.StyledString;
import com.tokeninc.deviceinfo.DeviceInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalePrintHelper extends BasePrintHelper{

    public String getFormattedText(SampleReceipt receipt, TransactionEntity transactionEntity, TransactionCode transactionCode,
                                   SlipType slipType, Context context, Integer ZNO, Integer ReceiptNo) {
        StyledString styledText = new StyledString();
        if (slipType == SlipType.CARDHOLDER_SLIP) {
            if (!((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name()) && !((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())) {
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
        } else if (slipType == SlipType.MERCHANT_SLIP) {
            styledText.addTextToLine("İŞYERİ NÜSHASI", Alignment.Center);
            styledText.newLine();
        }

        if (transactionCode == TransactionCode.VOID) {
            String transactionType = "";
            switch (transactionEntity.getbTransCode()) {
                case 1:
                    transactionType = "SATIŞ İPTALİ";
                    break;
                case 3:
                    transactionType = "İPTAL İŞLEMİ";
                    break;
                case 4:
                    transactionType = "E. İADE İPTALİ";
                    break;
                case 5:
                    transactionType = "N. İADE İPTALİ";
                    break;
                case 6:
                    transactionType = "T. İADE İPTALİ";
                    break;
            }
            styledText.addTextToLine(transactionType, PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.SALE) {
            styledText.addTextToLine("SATIŞ", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.MATCHED_REFUND) {
            styledText.addTextToLine("E. İADE", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.INSTALLMENT_REFUND) {
            styledText.addTextToLine("T. SATIŞ İADE", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine(transactionEntity.getbInstCnt() + " TAKSİT", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.CASH_REFUND) {
            styledText.addTextToLine("NAKİT İADE", PrinterDefinitions.Alignment.Center);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault());
        String dateTime = sdf.format(Calendar.getInstance().getTime());
        String lineTime = "";
        if (transactionCode == TransactionCode.VOID) {
            lineTime = dateTime + " M OFFLINE";
        }
        if (transactionCode == TransactionCode.SALE) {
            lineTime = dateTime + " C ONLINE";
        }
        if (transactionCode == TransactionCode.MATCHED_REFUND) {
            lineTime = dateTime + " M ONLINE";
        }

        styledText.newLine();
        styledText.addTextToLine(lineTime, PrinterDefinitions.Alignment.Center);

        styledText.newLine();
        styledText.addTextToLine(receipt.getCardNo(), Alignment.Center);

        styledText.newLine();
        styledText.addTextToLine(receipt.getFullName(), Alignment.Center);

        styledText.newLine();
        SimpleDateFormat ddMMyy = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String date = ddMMyy.format(Calendar.getInstance().getTime());

        SimpleDateFormat hhmmss = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = hhmmss.format(Calendar.getInstance().getTime());

        styledText.addTextToLine(date);
        styledText.addTextToLine(time, PrinterDefinitions.Alignment.Right);

        styledText.setLineSpacing(1f);
        styledText.setFontSize(14);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);
        styledText.newLine();
        styledText.addTextToLine("TUTAR:");
        if (transactionCode == TransactionCode.MATCHED_REFUND || transactionCode == TransactionCode.INSTALLMENT_REFUND || transactionCode == TransactionCode.CASH_REFUND) {
            styledText.addTextToLine(StringHelper.getAmount(transactionEntity.getUlAmount2()), PrinterDefinitions.Alignment.Right);
        }
        else if (transactionEntity != null) {
            styledText.addTextToLine(StringHelper.getAmount(transactionEntity.getUlAmount()), PrinterDefinitions.Alignment.Right);
        } else {
            styledText.addTextToLine(receipt.getAmount(), PrinterDefinitions.Alignment.Right);
        }

        styledText.setLineSpacing(0.5f);
        styledText.setFontSize(10);
        styledText.newLine();

        if (transactionCode == TransactionCode.VOID) {
            styledText.addTextToLine("İPTAL EDİLMİŞTİR", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("===========================",PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("İŞLEM TEMASSIZ TAMAMLANMIŞTIR", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("MASTERCARD CONTACTLESS", PrinterDefinitions.Alignment.Center);
            if (slipType == SlipType.CARDHOLDER_SLIP) {
                styledText.newLine();
                String signature = "İŞ YERİ İMZA: _ _ _ _ _ _ _ _ _ _ _ _ _ _";
                styledText.addTextToLine(signature,PrinterDefinitions.Alignment.Center);
                styledText.newLine();
                styledText.addTextToLine("===========================",PrinterDefinitions.Alignment.Center);
            }
        }
        if (transactionCode == TransactionCode.MATCHED_REFUND || transactionCode == TransactionCode.INSTALLMENT_REFUND || transactionCode == TransactionCode.CASH_REFUND) {
            styledText.addTextToLine("MAL/HİZM İADE EDİLMİŞTİR", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            if (transactionCode != TransactionCode.CASH_REFUND) {
                styledText.addTextToLine("İŞLEM TARİHİ: " + transactionEntity.getBaTranDate2(),PrinterDefinitions.Alignment.Center);
            } else {
                styledText.addTextToLine("İŞLEM TARİHİ: " + transactionEntity.getBaTranDate(), PrinterDefinitions.Alignment.Center);
            }
            styledText.newLine();
            styledText.addTextToLine("ORJ. İŞ YERİ NO: " + receipt.getMerchantID(),PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("İŞLEM TEMASSIZ TAMAMLANMIŞTIR", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine("MASTERCARD CONTACTLESS", PrinterDefinitions.Alignment.Center);
            if (slipType == SlipType.CARDHOLDER_SLIP) {
                styledText.newLine();
                String signature = "İŞ YERİ İMZA: _ _ _ _ _ _ _ _ _ _ _ _ _ _";
                styledText.addTextToLine(signature, PrinterDefinitions.Alignment.Center);
            }
        }
        if (transactionCode == TransactionCode.SALE) {
            if (slipType == SlipType.CARDHOLDER_SLIP) {
                styledText.addTextToLine("KARŞILIĞI MAL/HİZM ALDIM", Alignment.Center);
            } else {
                styledText.addTextToLine("İşlem Şifre Girilerek Yapılmıştır", Alignment.Center);
                styledText.newLine();
                styledText.addTextToLine("İMZAYA GEREK YOKTUR", Alignment.Center);
            }
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
            styledText.addTextToLine("REF NO: " + transactionEntity.getRefNo(), PrinterDefinitions.Alignment.Right);
            styledText.newLine();
            styledText.addTextToLine("AID: " + transactionEntity.getAid());
            if (transactionEntity.getAidLabel() != null) {
                styledText.addTextToLine(transactionEntity.getAidLabel(), PrinterDefinitions.Alignment.Right);
            }
        } else {
            styledText.addTextToLine("GRUP NO:" + 3);
            styledText.addTextToLine("REF NO: " + ((int) (Math.random() * 90000000) + 10000000), Alignment.Right);
            styledText.newLine();
            styledText.addTextToLine("AID: " + receipt.getAid());
            styledText.addTextToLine("42414E4B41204B41525449", PrinterDefinitions.Alignment.Right);
        }
        styledText.newLine();
        styledText.addTextToLine("Ver: 92.12.05");

        if (transactionCode == TransactionCode.SALE) {
            if (slipType == SlipType.MERCHANT_SLIP) {
                addTextToNewLine(styledText, "*MALİ DEĞERİ YOKTUR*", Alignment.Center, 8);
            }

            if (slipType == SlipType.MERCHANT_SLIP) {
                if (((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())) {
                    styledText.newLine();
                    styledText.addTextToLine("Z NO: " +ZNO, Alignment.Right);
                    styledText.addTextToLine("FİŞ NO: " +ReceiptNo, Alignment.Left);
                }
            }

            if (slipType == SlipType.MERCHANT_SLIP) {
                if (((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())
                        || ((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())) {
                    addTextToNewLine(styledText, ((AppTemp) context.getApplicationContext()).getCurrentFiscalID(), Alignment.Center, 8);
                }
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
