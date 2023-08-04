package com.example.application_template_jmvvm.utils.printHelpers;

import android.content.Context;

import com.example.application_template_jmvvm.AppTemp;
import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
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
    public String getFormattedText(SampleReceipt receipt, Transaction transaction, TransactionCode transactionCode,
                                   SlipType slipType, Context context, String ZNO, String receiptNo, boolean isCopy) {
        StyledString styledText = new StyledString();
        styledText.setFontSize(12);
        if (slipType == SlipType.CARDHOLDER_SLIP) {
            if (((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.GIB.name())) {
                printSlipHeader(styledText, receipt);
            }
        } else {
            printSlipHeader(styledText, receipt);
        }

        if (isCopy) {
            addTextToNewLine(styledText, "İKİNCİ KOPYA", PrinterDefinitions.Alignment.Center);
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
            switch (transaction.getbTransCode()) {
                case 1:
                    transactionType = "SATIŞ İPTALİ";
                    break;
                case 2:
                    transactionType = "T. SATIŞ İPTALİ";
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
        if (transactionCode == TransactionCode.INSTALLMENT_SALE) {
            styledText.addTextToLine("T. SATIŞ", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.MATCHED_REFUND) {
            styledText.addTextToLine("E. İADE", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.INSTALLMENT_REFUND) {
            styledText.addTextToLine("T. SATIŞ İADE", PrinterDefinitions.Alignment.Center);
            styledText.newLine();
            styledText.addTextToLine(transaction.getbInstCnt() + " TAKSİT", PrinterDefinitions.Alignment.Center);
        }
        if (transactionCode == TransactionCode.CASH_REFUND) {
            styledText.addTextToLine("NAKİT İADE", PrinterDefinitions.Alignment.Center);
        }

        if ((slipType == SlipType.CARDHOLDER_SLIP && ((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.GIB.name()))
            || slipType == SlipType.MERCHANT_SLIP) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            String lineTime;
            if (transactionCode == TransactionCode.VOID) {
                lineTime = dateTime + " M OFFLINE";
            }
            else if (transactionCode == TransactionCode.SALE || transactionCode == TransactionCode.INSTALLMENT_SALE) {
                lineTime = dateTime + " C ONLINE";
            }
            else if (transactionCode == TransactionCode.MATCHED_REFUND) {
                lineTime = dateTime + " M ONLINE";
            } else {
                lineTime = dateTime;
            }
            styledText.newLine();
            styledText.addTextToLine(lineTime, PrinterDefinitions.Alignment.Center);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            String lineTime;
            if (transactionCode == TransactionCode.VOID) {
                lineTime = dateTime + " M OFFLINE";
            }
            else if (transactionCode == TransactionCode.SALE || transactionCode == TransactionCode.INSTALLMENT_SALE) {
                lineTime = dateTime + " C ONLINE";
            }
            else if (transactionCode == TransactionCode.MATCHED_REFUND) {
                lineTime = dateTime + " M ONLINE";
            } else {
                lineTime = dateTime;
            }
            styledText.newLine();
            styledText.addTextToLine(lineTime, PrinterDefinitions.Alignment.Center);
        }

        styledText.newLine();
        styledText.addTextToLine(receipt.getCardNo(), Alignment.Center);

        if (transaction != null) {
            if (receipt.getFullName() != null) {
                styledText.newLine();
                styledText.addTextToLine(receipt.getFullName(), Alignment.Center);
            }
        }

        if (transactionCode == TransactionCode.VOID) {
            styledText.newLine();
            styledText.addTextToLine(DateUtil.getFormattedDate(transaction.getBaTranDate().substring(0, 8)));
            styledText.addTextToLine(DateUtil.getFormattedTime(transaction.getBaTranDate().substring(8)), PrinterDefinitions.Alignment.Right);
        }

        styledText.setLineSpacing(1f);
        styledText.setFontSize(14);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);
        styledText.newLine();
        styledText.addTextToLine("TUTAR:");
        if (transactionCode == TransactionCode.MATCHED_REFUND || transactionCode == TransactionCode.INSTALLMENT_REFUND || transactionCode == TransactionCode.CASH_REFUND) {
            styledText.addTextToLine(StringHelper.getAmount(transaction.getUlAmount2()), PrinterDefinitions.Alignment.Right);
        }
        else if (transaction != null) {
            styledText.addTextToLine(StringHelper.getAmount(transaction.getUlAmount()), PrinterDefinitions.Alignment.Right);
            if (transactionCode == TransactionCode.INSTALLMENT_SALE) {
                styledText.setFontSize(11);
                styledText.newLine();
                styledText.addTextToLine(transaction.getbInstCnt() + " x " + StringHelper.getInstAmount(transaction.getUlAmount()/(transaction.getbInstCnt())), PrinterDefinitions.Alignment.Center);
            }
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
                styledText.addTextToLine("ORJ. İŞLEM TARİHİ: " + transaction.getBaTranDate2(),PrinterDefinitions.Alignment.Center);
            } else {
                styledText.addTextToLine("ORJ. İŞLEM TARİHİ: " + DateUtil.getFormattedDate(transaction.getBaTranDate().substring(0, 8)), PrinterDefinitions.Alignment.Center);
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
        if (transactionCode == TransactionCode.SALE || transactionCode == TransactionCode.INSTALLMENT_SALE) {
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
        styledText.addTextToLine("SN: " + receipt.getSerialNo());
        if (transaction != null) {
            styledText.addTextToLine("ONAY KODU: " + receipt.getAuthCode(), Alignment.Right);
        } else {
            styledText.addTextToLine("ONAY KODU: " + ((int) (Math.random() * 90000) + 10000), Alignment.Right);
        }

        styledText.setFontSize(8);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);
        styledText.newLine();

        if (transaction != null) {
            styledText.addTextToLine("GRUP NO:" + receipt.getGroupNo());
            styledText.addTextToLine("REF NO: " + receipt.getRefNo(), PrinterDefinitions.Alignment.Right);
            styledText.newLine();
            styledText.addTextToLine("AID: " + receipt.getAid());
            if (receipt.getAidLabel() != null) {
                styledText.addTextToLine(receipt.getAidLabel(), PrinterDefinitions.Alignment.Right);
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

        if (transactionCode == TransactionCode.SALE || transactionCode == TransactionCode.INSTALLMENT_SALE) {
            if (slipType == SlipType.MERCHANT_SLIP) {
                if (((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())
                        || ((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.VUK507.name())) {
                    addTextToNewLine(styledText, "*MALİ DEĞERİ YOKTUR*", Alignment.Center, 8);
                }
            }

            if (slipType == SlipType.MERCHANT_SLIP) {
                if (((AppTemp) context.getApplicationContext()).getCurrentDeviceMode().equals(DeviceInfo.PosModeEnum.ECR.name())) {
                    styledText.newLine();
                    styledText.addTextToLine("Z NO: " + ZNO, Alignment.Right);
                    styledText.addTextToLine("FİŞ NO: " + receiptNo, Alignment.Left);
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
