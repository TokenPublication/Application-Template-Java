package com.example.application_template_jmvvm.data.repository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.model.type.PaymentTypes;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.example.application_template_jmvvm.utils.printHelpers.TransactionPrintHelper;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * This class for handle the operations for Transaction like intent, slip and response
 * Also it has Transaction DAO for perform database operations.
 */
public class TransactionRepository {
    private TransactionDao transactionDao;

    @Inject
    public TransactionRepository(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public List<Transaction> getTransactionsByRefNo(String refNo) {
        return transactionDao.getTransactionsByRefNo(refNo);
    }

    public List<Transaction> getTransactionsByCardNo(String cardNo) {
        return transactionDao.getTransactionsByCardNo(cardNo);
    }

    public void insertTransaction(Transaction transaction) {
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

    /**
     * It parses the response in a dummy way.
     */
    public OnlineTransactionResponse parseResponse() {
        OnlineTransactionResponse onlineTransactionResponse = new OnlineTransactionResponse();
        onlineTransactionResponse.setmResponseCode(ResponseCode.SUCCESS);
        onlineTransactionResponse.setmTextPrintCode("Test Print");
        onlineTransactionResponse.setmAuthCode(String.valueOf((int) (Math.random() * 900000) + 100000));
        onlineTransactionResponse.setmRefNo(String.valueOf((long) (Math.random() * 900000000) + (1000000000L * (int) (Math.random() * 9) + 1)));
        onlineTransactionResponse.setmDisplayData("Display Data");
        onlineTransactionResponse.setmKeySequenceNumber("3");
        onlineTransactionResponse.setDateTime(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date()));
        if (onlineTransactionResponse.getmResponseCode() == ResponseCode.SUCCESS) { //Dummy Response, always success
            Log.d("Confirmation Code:", onlineTransactionResponse.getmAuthCode());
        }
        return onlineTransactionResponse;
    }

    /**
     * This method puts required values to Transaction object for insert the transaction into Transaction DB
     */
    public Transaction entityCreator(ICCCard card, Bundle bundle, OnlineTransactionResponse onlineTransactionResponse, TransactionCode transactionCode) {
        Transaction transaction = new Transaction();
        transaction.setUuid(bundle.getString("UUID"));
        transaction.setReceiptNo(bundle.getString("ReceiptNo"));
        transaction.setZNO(bundle.getString("ZNO"));
        transaction.setUlAmount(card.getmTranAmount1());
        transaction.setbCardReadType(card.getmCardReadType());
        transaction.setbTransCode(transactionCode.getType());
        transaction.setBaPAN(card.getmCardNumber());
        transaction.setBaExpDate(card.getmExpireDate());
        transaction.setBaCustomerName(card.getmTrack1CustomerName());
        transaction.setBaTranDate(onlineTransactionResponse.getDateTime());
        transaction.setBaTrack2(card.getmTrack2Data());
        transaction.setBaRspCode(onlineTransactionResponse.getmResponseCode().ordinal());
        transaction.setIsVoid(0);
        transaction.setRefNo(onlineTransactionResponse.getmRefNo());
        transaction.setStPrintData(onlineTransactionResponse.getmTextPrintCode());
        transaction.setAuthCode(onlineTransactionResponse.getmAuthCode());
        transaction.setAid(card.getAID2());
        transaction.setAidLabel(card.getAIDLabel());
        transaction.setPinByPass(card.isPinByPass() ? 1 : 0);
        transaction.setDisplayData(onlineTransactionResponse.getmDisplayData());
        transaction.setBaCVM(card.getCVM());
        transaction.setAC(card.getAC());
        transaction.setCID(card.getCID());
        transaction.setATC(card.getATC());
        transaction.setTVR(card.getTVR());
        transaction.setTSI(card.getTSI());
        transaction.setAIP(card.getAIP());
        transaction.setIsOffline(0); //TODO Developer, check for offline transaction
        transaction.setSID(card.getSID());
        transaction.setIsOnlinePIN(card.getOnlPINReq());
        switch (transactionCode) {
            case INSTALLMENT_SALE:
                transaction.setbInstCnt(bundle.getInt(ExtraContentInfo.instCount));
                break;
            case MATCHED_REFUND:
                transaction.setUlAmount(bundle.getInt(ExtraContentInfo.orgAmount));
                transaction.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                transaction.setBaTranDate2(bundle.getString(ExtraContentInfo.tranDate));
                break;
            case CASH_REFUND:
                transaction.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                transaction.setBaTranDate2(bundle.getString(ExtraContentInfo.tranDate));
                break;
            case INSTALLMENT_REFUND:
                transaction.setUlAmount(bundle.getInt(ExtraContentInfo.orgAmount));
                transaction.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                transaction.setBaTranDate2(bundle.getString(ExtraContentInfo.tranDate));
                transaction.setbInstCnt(bundle.getInt(ExtraContentInfo.instCount));
                break;
            default:
                break;
        }
        return transaction;
    }

    /**
     * This method puts required values to bundle (something like contentValues for data transferring).
     * After that, an intent will be created with this bundle to provide communication between GiB and Application Template via IPC
     */
    public Intent prepareSaleIntent(SampleReceipt receipt, MainActivity mainActivity, Transaction transaction,
                                TransactionCode transactionCode, ResponseCode responseCode, String ZNO, String receiptNo) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        int amount = transaction.getUlAmount();
        SlipType slipType = SlipType.BOTH_SLIPS;
        String cardNo = transaction.getBaPAN();
        bundle.putInt("ResponseCode", responseCode.ordinal()); // #1 Response Code
        bundle.putString("CardOwner", transaction.getBaCustomerName()); // Optional
        bundle.putString("CardNumber", cardNo); // Optional, Card No can be masked
        bundle.putInt("PaymentStatus", 0); // #2 Payment Status
        bundle.putInt("Amount", amount); // #3 Amount
        bundle.putInt("BatchNo", transaction.getBatchNo());
        bundle.putString("CardNo", StringHelper.MaskTheCardNo(transaction.getBaPAN()));
        bundle.putString("MID", receipt.getMerchantID()); //#6 Merchant ID
        bundle.putString("TID", receipt.getPosID()); //#7 Terminal ID
        bundle.putInt("TxnNo", transaction.getUlGUP_SN());
        bundle.putInt("SlipType", slipType.value);
        bundle.putBoolean("IsSlip", true);
        bundle.putString("RefNo", transaction.getRefNo());
        bundle.putString("AuthNo", transaction.getAuthCode());
        bundle.putInt("PaymentType", PaymentTypes.CREDITCARD.getType());
        TransactionPrintHelper transactionPrintHelper = new TransactionPrintHelper();
        if (responseCode == ResponseCode.CANCELLED || responseCode == ResponseCode.UNABLE_DECLINE || responseCode == ResponseCode.OFFLINE_DECLINE) {
            slipType = SlipType.NO_SLIP;
            //TODO Developer, no slip or cancel slip.
        } else {
            if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
                bundle.putString("customerSlipData", transactionPrintHelper.getFormattedText(receipt, transaction, transactionCode, SlipType.CARDHOLDER_SLIP, mainActivity, ZNO, receiptNo, false));
            }
            if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
                bundle.putString("merchantSlipData", transactionPrintHelper.getFormattedText(receipt, transaction, transactionCode, SlipType.MERCHANT_SLIP, mainActivity, ZNO, receiptNo, false));
            }
            bundle.putString("RefundInfo", getRefundInfo(transaction, cardNo, amount, receipt));
            if (transactionCode == TransactionCode.MATCHED_REFUND || transactionCode == TransactionCode.CASH_REFUND || transactionCode == TransactionCode.INSTALLMENT_REFUND || transactionCode == TransactionCode.VOID) {
                printSlip(bundle.getString("customerSlipData"), mainActivity);
                printSlip(bundle.getString("merchantSlipData"), mainActivity);
            }
        }
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * It prepares refund and void intent for only gib and print slip
     */
    public Intent prepareIntent(SampleReceipt receipt, MainActivity mainActivity, Transaction transaction,
                                    TransactionCode transactionCode, ResponseCode responseCode) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        bundle.putInt("ResponseCode", responseCode.ordinal());
        prepareSlip(receipt, mainActivity, transaction, transactionCode, false);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * It prepares and prints the slip.
     */
    public void prepareSlip(SampleReceipt receipt, MainActivity mainActivity, Transaction transaction,
                            TransactionCode transactionCode, boolean isCopy) {
        TransactionPrintHelper transactionPrintHelper = new TransactionPrintHelper();
        String customerSlipData = transactionPrintHelper.getFormattedText(receipt, transaction, transactionCode, SlipType.CARDHOLDER_SLIP, mainActivity, transaction.getZNO(), transaction.getReceiptNo(), isCopy);
        String merchantSlipData = transactionPrintHelper.getFormattedText(receipt, transaction, transactionCode, SlipType.MERCHANT_SLIP, mainActivity, transaction.getZNO(), transaction.getReceiptNo(), isCopy);
        printSlip(customerSlipData, mainActivity);
        printSlip(merchantSlipData, mainActivity);
    }

    public void printSlip(String printText, MainActivity mainActivity) {
        StyledString styledText = new StyledString();
        styledText.addStyledText(printText);
        styledText.finishPrintingProcedure();
        styledText.print(PrinterService.getService(mainActivity.getApplicationContext()));
    }

    public Intent prepareDummyResponse(ActivationRepository activationRepository, BatchRepository batchRepository,
                                     MainActivity mainActivity, Integer price, ResponseCode code, Boolean hasSlip,
                                     SlipType slipType, String cardNo, String ownerName, int paymentType) {
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
        Transaction transaction = new Transaction();
        transaction.setBaPAN(cardNo);
        transaction.setBaCustomerName(ownerName);
        transaction.setUlAmount(price);
        SampleReceipt receipt = new SampleReceipt(transaction, activationRepository, batchRepository, null);
        bundle.putString("RefundInfo", getRefundInfo(null, cardNo, price, receipt));
        bundle.putString("RefNo", String.valueOf(32134323));
        bundle.putInt("PaymentType", paymentType);
        TransactionPrintHelper transactionPrintHelper = new TransactionPrintHelper();
        if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("customerSlipData", transactionPrintHelper.getDummyFormattedText(receipt, TransactionCode.SALE, SlipType.CARDHOLDER_SLIP, mainActivity, "1", "1"));
        }
        if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("merchantSlipData", transactionPrintHelper.getDummyFormattedText(receipt, TransactionCode.SALE, SlipType.MERCHANT_SLIP, mainActivity, "1", "1"));
        }
        if (slipType == SlipType.NO_SLIP) {
            bundle.putString("customerSlipData", "");
            bundle.putString("merchantSlipData", "");
        }
        resultIntent.putExtras(bundle);
        return resultIntent;
    }

    /**
     * @return refundInfo which is Json with necessary components
     */
    private String getRefundInfo(Transaction transaction, String cardNumber, int amount, SampleReceipt receipt) {
        JSONObject json = new JSONObject();
        try {
            json.put("BatchNo", receipt.getGroupNo());
            json.put("TxnNo", receipt.getSerialNo());
            json.put("Amount", amount);
            json.put("MID", receipt.getMerchantID());
            json.put("TID", receipt.getPosID());
            json.put("CardNo", StringHelper.MaskTheCardNo(cardNumber));
            if (transaction != null) {
                json.put("RefNo", transaction.getRefNo());
                json.put("AuthCode", transaction.getAuthCode());
                json.put("TranDate", transaction.getBaTranDate());
                if (transaction.getbInstCnt() > 0) {
                    json.put("InstCount", transaction.getbInstCnt());
                    json.put("InstAmount", transaction.getUlAmount()/transaction.getbInstCnt());
                } else {
                    json.put("InstCount", 0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
