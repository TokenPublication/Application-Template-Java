package com.example.application_template_jmvvm.utils.printHelpers;

import android.content.Context;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.token.printerlib.PrinterDefinitions;
import com.token.printerlib.StyledString;
import com.tokeninc.deviceinfo.DeviceInfo;

import java.util.List;

public class BatchClosePrintHelper extends BasePrintHelper {

    public String batchText(Context context, String batch_no, ActivationRepository activationRepository, List<TransactionEntity> transactions, boolean isCopy) {
        StyledString styledText = new StyledString();
        PrintHelper printHelper = new PrintHelper();
        int totalAmount = 0;
        String MID = activationRepository.getMerchantId();
        String TID = activationRepository.getTerminalId();

        addTextToNewLine(styledText, "TOKEN", PrinterDefinitions.Alignment.Center);
        addTextToNewLine(styledText, "FINTECH", PrinterDefinitions.Alignment.Center);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Bold);
        addTextToNewLine(styledText, "İŞYERİ NO: ", PrinterDefinitions.Alignment.Left);
        addText(styledText, MID, PrinterDefinitions.Alignment.Right);
        addTextToNewLine(styledText, "TERMİNAL NO: ", PrinterDefinitions.Alignment.Left);
        addText(styledText, TID, PrinterDefinitions.Alignment.Right);
        styledText.setFontFace(PrinterDefinitions.Font_E.Sans_Semi_Bold);

        if (isCopy) {
            addTextToNewLine(styledText, "İKİNCİ KOPYA", PrinterDefinitions.Alignment.Center, 12);
        }

        addTextToNewLine(styledText, "Ver. :", PrinterDefinitions.Alignment.Left);
        addText(styledText, String.valueOf(DeviceInfo.Field.LYNX_VERSION), PrinterDefinitions.Alignment.Right);
        addTextToNewLine(styledText, "DETAY", PrinterDefinitions.Alignment.Center);
        addTextToNewLine(styledText, "İŞLEMLER LİSTESİ", PrinterDefinitions.Alignment.Center);
        addTextToNewLine(styledText, "===========================", PrinterDefinitions.Alignment.Center);
        addTextToNewLine(styledText, "PEŞİN İŞLEMLER", PrinterDefinitions.Alignment.Left);

        for (TransactionEntity transaction : transactions) {
            addTextToNewLine(styledText, transaction.getBaTranDate(), PrinterDefinitions.Alignment.Left);

            String transactionType = "";
            switch (transaction.getbTransCode()) {
                case 1:
                    transactionType = "SATIŞ ";
                    break;
                case 2:
                    transactionType = "T. SATIŞ ";
                    break;
                case 4:
                    transactionType = "E. İADE ";
                    break;
                case 5:
                    transactionType = "N. İADE ";
                    break;
                case 6:
                    transactionType = "T. İADE ";
                    break;
            }

            if (transaction.getIsVoid() == 1) {
                transactionType = "İPTAL ";
            }

            addText(styledText, transactionType + transaction.getUlGUP_SN(), PrinterDefinitions.Alignment.Right);
            addTextToNewLine(styledText, StringHelper.MaskTheCardNo(transaction.getBaPAN()), PrinterDefinitions.Alignment.Left);
            addText(styledText, transaction.getBaExpDate(), PrinterDefinitions.Alignment.Right);
            addTextToNewLine(styledText, transaction.getRefNo(), PrinterDefinitions.Alignment.Left);

            int amount = transaction.getUlAmount();
            totalAmount += amount;
            addText(styledText, StringHelper.getAmount(amount), PrinterDefinitions.Alignment.Right);
            styledText.newLine();
        }

        addTextToNewLine(styledText, "İŞLEM SAYISI:", PrinterDefinitions.Alignment.Left);
        addText(styledText, String.valueOf(transactions.size()), PrinterDefinitions.Alignment.Right);
        addTextToNewLine(styledText, "TOPLAM:", PrinterDefinitions.Alignment.Left);
        addText(styledText, StringHelper.getAmount(totalAmount), PrinterDefinitions.Alignment.Right);
        styledText.newLine();
        addTextToNewLine(styledText, "===========================", PrinterDefinitions.Alignment.Center);
        styledText.newLine();
        styledText.printLogo(context);
        styledText.addSpace(50);

        printHelper.PrintBatchClose(styledText, batch_no, String.valueOf(transactions.size()), totalAmount, MID, TID);

        addTextToNewLine(styledText, "BU BELGEYİ SAKLAYINIZ", PrinterDefinitions.Alignment.Center, 8);
        styledText.newLine();
        styledText.printLogo(context);
        styledText.addSpace(50);

        return styledText.toString();
    }

}
