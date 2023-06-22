package com.example.application_template_jmvvm.data.repository;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCols;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.domain.SampleReceipt;
import com.example.application_template_jmvvm.domain.extraContentInfo;
import com.example.application_template_jmvvm.domain.printHelpers.StringHelper;
import com.example.application_template_jmvvm.domain.printHelpers.SalePrintHelper;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;
import com.token.uicomponents.CustomInput.CustomInputFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TransactionRepository {

    private TransactionDao transactionDao;

    @Inject
    public TransactionRepository(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<TransactionEntity> getAllTransactions(){
        return transactionDao.getAllTransactions();
    }

    public List<TransactionEntity> getTransactionsByRefNo(String refNo) {
        return transactionDao.getTransactionsByRefNo(refNo);
    }

    public List<TransactionEntity> getTransactionsByCardNo(String cardNo) {
        return transactionDao.getTransactionsByCardNo(cardNo);
    }

    public void insertTransaction(TransactionEntity transaction) {
        transactionDao.insertTransaction(transaction);
    }

    public void setVoid(int gupSN, String date, String card_SID) {
        transactionDao.setVoid(gupSN, date, card_SID);
    }

    public boolean isEmpty() {
        return transactionDao.isEmpty() == 0;
    }

    public void deleteAll() {
        transactionDao.deleteAll();
    }

    public OnlineTransactionResponse parseResponse(TransactionViewModel transactionViewModel) {
        OnlineTransactionResponse onlineTransactionResponse = new OnlineTransactionResponse();
        onlineTransactionResponse.setmResponseCode(ResponseCode.SUCCESS);
        onlineTransactionResponse.setmTextPrintCode1("Test Print 1");
        onlineTransactionResponse.setmTextPrintCode2("Test Print 2");
        onlineTransactionResponse.setmAuthCode(String.valueOf((int) (Math.random() * 900000) + 100000));
        onlineTransactionResponse.setmRefNo(String.valueOf((long) (Math.random() * 900000000) + (1000000000L * (int) (Math.random() * 9) + 1)));
        onlineTransactionResponse.setmDisplayData("Display Data");
        onlineTransactionResponse.setmKeySequenceNumber("3");
        onlineTransactionResponse.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (onlineTransactionResponse.getmResponseCode() == ResponseCode.SUCCESS)  //Dummy Response, always success
            transactionViewModel.setShowDialogLiveData("ONAY KODU: " + onlineTransactionResponse.getmAuthCode()); //TODO liveData
        return onlineTransactionResponse;
    }

    public TransactionEntity entityCreator(ICCCard card, String uuid, Bundle bundle, OnlineTransactionResponse onlineTransactionResponse,
                                           TransactionCode transactionCode){
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUuid(uuid);
        transactionEntity.setUlSTN("STN");
        transactionEntity.setUlAmount(card.getmTranAmount1());
        transactionEntity.setbCardReadType(card.getmCardReadType());
        transactionEntity.setbTransCode(transactionCode.getType());
        transactionEntity.setBaPAN(card.getmCardNumber());
        transactionEntity.setBaExpDate(card.getmExpireDate());
        transactionEntity.setBaDate(card.getDateTime().substring(0, 8));
        transactionEntity.setBaTime(card.getDateTime().substring(8));
        transactionEntity.setBaTrack2(card.getmTrack2Data());
        transactionEntity.setBaRspCode(onlineTransactionResponse.getmResponseCode().toString());
        transactionEntity.setIsVoid(0);
        transactionEntity.setBaTranDate(card.getDateTime());
        transactionEntity.setRefNo(onlineTransactionResponse.getmRefNo());
        transactionEntity.setIsSignature(0);
        transactionEntity.setStPrintData1(onlineTransactionResponse.getmTextPrintCode1());
        transactionEntity.setStPrintData2(onlineTransactionResponse.getmTextPrintCode2());
        transactionEntity.setAuthCode(onlineTransactionResponse.getmAuthCode());
        transactionEntity.setAid(card.getAID2());
        transactionEntity.setAidLabel(card.getAIDLabel());
        transactionEntity.setPinByPass(0);
        transactionEntity.setDisplayData(onlineTransactionResponse.getmDisplayData());
        transactionEntity.setBaCVM(card.getCVM());
        transactionEntity.setIsOffline(0);
        transactionEntity.setSID(card.getSID());
        transactionEntity.setIsOnlinePIN(0);
        switch (transactionCode) {
            case MATCHED_REFUND:
                transactionEntity.setUlAmount(bundle.getInt(extraContentInfo.orgAmount));
                transactionEntity.setUlAmount2(bundle.getInt(extraContentInfo.refAmount));
                transactionEntity.setRefNo(bundle.getString(extraContentInfo.refNo));
                transactionEntity.setAuthCode(bundle.getString(extraContentInfo.authCode));
                transactionEntity.setBaTranDate2(bundle.getString(extraContentInfo.tranDate));
                break;
            case CASH_REFUND:
                transactionEntity.setUlAmount2(bundle.getInt(extraContentInfo.refAmount));
                break;
            case INSTALLMENT_REFUND:
                transactionEntity.setUlAmount(bundle.getInt(extraContentInfo.orgAmount));
                transactionEntity.setUlAmount2(bundle.getInt(extraContentInfo.refAmount));
                transactionEntity.setRefNo(bundle.getString(extraContentInfo.refNo));
                transactionEntity.setAuthCode(bundle.getString(extraContentInfo.authCode));
                transactionEntity.setBaTranDate2(bundle.getString(extraContentInfo.tranDate));
                transactionEntity.setbInstCnt(bundle.getInt(extraContentInfo.instCount));
                break;
            default:
                // Handle other refund types or provide a default behavior
                break;
        }
        return transactionEntity;
    }

    public Intent prepareIntent(ActivationRepository activationRepository, BatchRepository batchRepository,
                            MainActivity mainActivity, Fragment fragment, TransactionEntity transactionEntity,
                                TransactionCode transactionCode, ResponseCode responseCode){
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        int amount = transactionEntity.getUlAmount();
        SlipType slipType = SlipType.BOTH_SLIPS;
        String cardNo = transactionEntity.getBaPAN();
        bundle.putInt("ResponseCode", responseCode.ordinal()); // #1 Response Code
        bundle.putString("CardOwner", "OWNER NAME"); // Optional
        bundle.putString("CardNumber", cardNo); // Optional, Card No can be masked
        bundle.putInt("PaymentStatus", 0); // #2 Payment Status
        bundle.putInt("Amount", amount); // #3 Amount
        bundle.putInt("BatchNo", transactionEntity.getBatchNo());
        bundle.putString("CardNo", StringHelper.MaskTheCardNo(transactionEntity.getBaPAN())); //#5 Card No "MASKED"
        bundle.putString("MID", activationRepository.getMerchantId()); //#6 Merchant ID
        bundle.putString("TID", activationRepository.getTerminalId()); //#7 Terminal ID
        bundle.putInt("TxnNo", transactionEntity.getUlGUP_SN());
        bundle.putInt("SlipType", slipType.value);
        bundle.putBoolean("IsSlip", true);
        bundle.putString("RefNo", transactionEntity.getRefNo());
        bundle.putString("AuthNo", transactionEntity.getAuthCode());
        bundle.putInt("PaymentType", 3);
        SalePrintHelper salePrintHelper = new SalePrintHelper();
        if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("customerSlipData", salePrintHelper.getFormattedText(getSampleReceipt(cardNo, "OWNER NAME", amount, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.CARDHOLDER_SLIP, mainActivity, 1, 2));
        }
        if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("merchantSlipData", salePrintHelper.getFormattedText(getSampleReceipt(cardNo, "OWNER NAME", amount, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.MERCHANT_SLIP, mainActivity, 1, 2));
        }
        if (transactionCode == TransactionCode.MATCHED_REFUND || transactionCode == TransactionCode.CASH_REFUND || transactionCode == TransactionCode.INSTALLMENT_REFUND || transactionCode == TransactionCode.VOID) {
            printSlip(bundle.getString("customerSlipData"), mainActivity);
            printSlip(bundle.getString("merchantSlipData"), mainActivity);
        }
        intent.putExtras(bundle);
        return intent;
    }

    public void printSlip(String printText, MainActivity mainActivity) {
        StyledString styledText = new StyledString();
        styledText.addStyledText(printText);
        styledText.finishPrintingProcedure();
        styledText.print(PrinterService.getService(mainActivity.getApplicationContext()));
    }

    public ContentValues prepareContentValues(ICCCard card, String uuid, TransactionCode transactionCode) {
        ContentValues values = new ContentValues();
        values.put(TransactionCols.col_uuid, uuid);
        values.put(TransactionCols.col_ulSTN, "STN");
        values.put(TransactionCols.col_bCardReadType, card.getmCardReadType());
        values.put(TransactionCols.col_bTransCode, transactionCode.getType());
        values.put(TransactionCols.col_ulAmount, card.getmTranAmount1());
        values.put(TransactionCols.col_baPAN, card.getmCardNumber());
        values.put(TransactionCols.col_baExpDate, card.getmExpireDate());
        values.put(TransactionCols.col_baDate, card.getDateTime().substring(0, 8));
        values.put(TransactionCols.col_baTime, card.getDateTime().substring(8));
        values.put(TransactionCols.col_baTrack2, card.getmTrack2Data());
        values.put(TransactionCols.col_baCustomName, card.getmTrack1CustomerName());
        values.put(TransactionCols.col_baRspCode, 3);
        values.put(TransactionCols.col_baTranDate, card.getDateTime());
        values.put(TransactionCols.col_aid, card.getAID2());
        values.put(TransactionCols.col_aidLabel, card.getAIDLabel());
        values.put(TransactionCols.col_baCVM, card.getCVM());
        values.put(TransactionCols.col_SID, card.getSID());
        return values;
    }

    public ContentValues putExtraContents(ContentValues values, TransactionCode transactionCode,
                                          List<CustomInputFormat> inputList) {
        switch (transactionCode) {
            case MATCHED_REFUND:
                values.put(TransactionCols.col_ulAmount,Integer.parseInt(inputList.get(0).getText()));
                values.put(TransactionCols.col_ulAmount2, Integer.parseInt(inputList.get(1).getText()));
                values.put(TransactionCols.col_authCode, inputList.get(3).getText());
                values.put(TransactionCols.col_baTranDate2, inputList.get(4).getText());
                break;
            case CASH_REFUND:
                values.put(TransactionCols.col_ulAmount2, Integer.parseInt(inputList.get(0).getText()));
                break;
            case INSTALLMENT_REFUND:
                // Handle installment refund type
                break;
            default:
                // Default
                break;
        }
        return values;
    }

    public void prepareDummyResponse(TransactionViewModel transactionViewModel, ActivationRepository activationRepository, BatchRepository batchRepository,
                                     MainActivity mainActivity, Fragment fragment, Integer price, ResponseCode code, Boolean hasSlip,
                                       SlipType slipType, String cardNo, String ownerName, int paymentType){
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("ResponseCode", code.ordinal()); // #1 Response Code

        bundle.putString("CardOwner", ownerName); // Optional
        bundle.putString("CardNumber", cardNo); // Optional, Card No can be masked
        bundle.putInt("PaymentStatus", 0); // #2 Payment Status
        bundle.putInt("Amount", price); // #3 Amount
        bundle.putInt("Amount2", price);
        bundle.putBoolean("IsSlip", hasSlip);
        bundle.putInt("BatchNo", batchRepository.getBatchNo());
        bundle.putString("CardNo", StringHelper.MaskTheCardNo(cardNo)); //#5 Card No "MASKED"
        bundle.putString("MID", activationRepository.getMerchantId()); //#6 Merchant ID
        bundle.putString("TID", activationRepository.getTerminalId()); //#7 Terminal ID
        bundle.putInt("TxnNo", batchRepository.getGroupSN());
        bundle.putInt("SlipType", slipType.value);

        bundle.putString("RefundInfo", getRefundInfo(ResponseCode.SUCCESS, cardNo, price, activationRepository, batchRepository));
        bundle.putString("RefNo", String.valueOf(32134323));
        bundle.putInt("PaymentType", paymentType);
        SalePrintHelper salePrintHelper = new SalePrintHelper();
        if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("customerSlipData", salePrintHelper.getFormattedText(getSampleReceipt(cardNo, ownerName, price, activationRepository, batchRepository), null, TransactionCode.SALE, SlipType.CARDHOLDER_SLIP, mainActivity, 1, 2));
        }
        if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("merchantSlipData", salePrintHelper.getFormattedText(getSampleReceipt(cardNo, ownerName, price, activationRepository, batchRepository), null, TransactionCode.SALE, SlipType.MERCHANT_SLIP, mainActivity, 1, 2));
        }
        resultIntent.putExtras(bundle);
        transactionViewModel.setIntentLiveData(resultIntent);
    }

    private String getRefundInfo(ResponseCode response, String cardNumber, int amount,
                                 ActivationRepository activationRepository, BatchRepository batchRepository) {
        JSONObject json = new JSONObject();
        try {
            json.put("BatchNo", batchRepository.getBatchNo());
            json.put("TxnNo", batchRepository.getGroupSN());
            json.put("Amount", amount);
            json.put("RefNo", String.valueOf(batchRepository.getGroupSN()));
            json.put("MID", activationRepository.getMerchantId());
            json.put("TID", activationRepository.getTerminalId());
            json.put("CardNo", StringHelper.MaskTheCardNo(cardNumber));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private SampleReceipt getSampleReceipt(String cardNo, String ownerName, int amount,
                                           ActivationRepository activationRepository, BatchRepository batchRepository) {
        SampleReceipt receipt = new SampleReceipt();
        receipt.setMerchantName("TOKEN FINTECH");
        receipt.setMerchantID(activationRepository.getMerchantId());
        receipt.setPosID(activationRepository.getTerminalId());
        receipt.setCardNo(StringHelper.maskCardNumber(cardNo));
        receipt.setFullName(ownerName);
        receipt.setAmount(StringHelper.getAmount(amount));
        receipt.setGroupNo(String.valueOf(batchRepository.getBatchNo()));
        receipt.setAid("A0000000000031010");
        receipt.setSerialNo(String.valueOf(batchRepository.getGroupSN()));
        receipt.setApprovalCode(StringHelper.GenerateApprovalCode(String.valueOf(batchRepository.getBatchNo()), String.valueOf(batchRepository.getGroupSN()), String.valueOf(batchRepository.getGroupSN()-1)));
        return receipt;
    }

}
