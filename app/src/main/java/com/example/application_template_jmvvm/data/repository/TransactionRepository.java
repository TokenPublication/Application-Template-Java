package com.example.application_template_jmvvm.data.repository;

import android.content.Intent;
import android.os.Bundle;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.data.model.type.PaymentTypes;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.example.application_template_jmvvm.utils.printHelpers.SalePrintHelper;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;
import com.token.uicomponents.infodialog.InfoDialog;

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

    public List<TransactionEntity> getAllTransactions() {
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

    public boolean isEmptyVoid() {
        return transactionDao.isEmptyVoid() == 0;
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
    public OnlineTransactionResponse parseResponse(TransactionViewModel transactionViewModel, MainActivity mainActivity) {
        OnlineTransactionResponse onlineTransactionResponse = new OnlineTransactionResponse();
        onlineTransactionResponse.setmResponseCode(ResponseCode.SUCCESS);
        onlineTransactionResponse.setmTextPrintCode("Test Print");
        onlineTransactionResponse.setmAuthCode(String.valueOf((int) (Math.random() * 900000) + 100000));
        onlineTransactionResponse.setmRefNo(String.valueOf((long) (Math.random() * 900000000) + (1000000000L * (int) (Math.random() * 9) + 1)));
        onlineTransactionResponse.setmDisplayData("Display Data");
        onlineTransactionResponse.setmKeySequenceNumber("3");
        onlineTransactionResponse.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        if (onlineTransactionResponse.getmResponseCode() == ResponseCode.SUCCESS) { //Dummy Response, always success
            transactionViewModel.setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getString(R.string.confirmation_code) + ": " + onlineTransactionResponse.getmAuthCode()));
        }
        return onlineTransactionResponse;
    }

    /**
     * This method puts required values to TransactionEntity object for insert the transaction into Transaction DB
     */
    public TransactionEntity entityCreator(ICCCard card, String uuid, Bundle bundle, OnlineTransactionResponse onlineTransactionResponse, TransactionCode transactionCode) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUuid(uuid);
        transactionEntity.setUlAmount(card.getmTranAmount1());
        transactionEntity.setbCardReadType(card.getmCardReadType());
        transactionEntity.setbTransCode(transactionCode.getType());
        transactionEntity.setBaPAN(card.getmCardNumber());
        transactionEntity.setBaExpDate(card.getmExpireDate());
        transactionEntity.setBaCustomerName(card.getmTrack1CustomerName());
        if (card.getmCardReadType() != CardReadType.ICC.getType()) {
            transactionEntity.setBaTranDate(card.getDateTime());
        } else {
            transactionEntity.setBaTranDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + " " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        }
        transactionEntity.setBaTrack2(card.getmTrack2Data());
        transactionEntity.setBaRspCode(onlineTransactionResponse.getmResponseCode().ordinal());
        transactionEntity.setIsVoid(0);
        transactionEntity.setRefNo(onlineTransactionResponse.getmRefNo());
        transactionEntity.setIsSignature(0);
        transactionEntity.setStPrintData(onlineTransactionResponse.getmTextPrintCode());
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
            case INSTALLMENT_SALE:
                transactionEntity.setbInstCnt(bundle.getInt(ExtraContentInfo.instCount));
                break;
            case MATCHED_REFUND:
                transactionEntity.setUlAmount(bundle.getInt(ExtraContentInfo.orgAmount));
                transactionEntity.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                transactionEntity.setRefNo(bundle.getString(ExtraContentInfo.refNo));
                transactionEntity.setAuthCode(bundle.getString(ExtraContentInfo.authCode));
                transactionEntity.setBaTranDate2(bundle.getString(ExtraContentInfo.tranDate));
                break;
            case CASH_REFUND:
                transactionEntity.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                break;
            case INSTALLMENT_REFUND:
                transactionEntity.setUlAmount(bundle.getInt(ExtraContentInfo.orgAmount));
                transactionEntity.setUlAmount2(bundle.getInt(ExtraContentInfo.refAmount));
                transactionEntity.setRefNo(bundle.getString(ExtraContentInfo.refNo));
                transactionEntity.setAuthCode(bundle.getString(ExtraContentInfo.authCode));
                transactionEntity.setBaTranDate2(bundle.getString(ExtraContentInfo.tranDate));
                transactionEntity.setbInstCnt(bundle.getInt(ExtraContentInfo.instCount));
                break;
            default:
                break;
        }
        return transactionEntity;
    }

    /**
     * This method puts required values to bundle (something like contentValues for data transferring).
     * After that, an intent will be created with this bundle to provide communication between GiB and Application Template via IPC
     */
    public Intent prepareSaleIntent(ActivationRepository activationRepository, BatchRepository batchRepository,
                                MainActivity mainActivity, TransactionEntity transactionEntity,
                                TransactionCode transactionCode, ResponseCode responseCode, String ZNO, String receiptNo) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        int amount = transactionEntity.getUlAmount();
        SlipType slipType = SlipType.BOTH_SLIPS;
        String cardNo = transactionEntity.getBaPAN();
        bundle.putInt("ResponseCode", responseCode.ordinal()); // #1 Response Code
        bundle.putString("CardOwner", transactionEntity.getBaCustomerName()); // Optional
        bundle.putString("CardNumber", cardNo); // Optional, Card No can be masked
        bundle.putInt("PaymentStatus", 0); // #2 Payment Status
        bundle.putInt("Amount", amount); // #3 Amount
        bundle.putInt("BatchNo", transactionEntity.getBatchNo());
        bundle.putString("CardNo", StringHelper.MaskTheCardNo(transactionEntity.getBaPAN()));
        bundle.putString("MID", activationRepository.getMerchantId()); //#6 Merchant ID
        bundle.putString("TID", activationRepository.getTerminalId()); //#7 Terminal ID
        bundle.putInt("TxnNo", transactionEntity.getUlGUP_SN());
        bundle.putInt("SlipType", slipType.value);
        bundle.putBoolean("IsSlip", true);
        bundle.putString("RefNo", transactionEntity.getRefNo());
        bundle.putString("AuthNo", transactionEntity.getAuthCode());
        bundle.putInt("PaymentType", PaymentTypes.CREDITCARD.getType());
        SalePrintHelper salePrintHelper = new SalePrintHelper();
        if (responseCode == ResponseCode.CANCELLED || responseCode == ResponseCode.UNABLE_DECLINE || responseCode == ResponseCode.OFFLINE_DECLINE) {
            slipType = SlipType.NO_SLIP;
            //TODO Developer, no slip or cancel slip.
        } else {
            if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
                bundle.putString("customerSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.CARDHOLDER_SLIP, mainActivity, ZNO, receiptNo, false));
            }
            if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
                bundle.putString("merchantSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.MERCHANT_SLIP, mainActivity, ZNO, receiptNo, false));
            }
            bundle.putString("RefundInfo", getRefundInfo(transactionEntity, cardNo, amount, activationRepository, batchRepository));
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
    public Intent prepareIntent(ActivationRepository activationRepository, BatchRepository batchRepository,
                                    MainActivity mainActivity, TransactionEntity transactionEntity,
                                    TransactionCode transactionCode, ResponseCode responseCode) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        bundle.putInt("ResponseCode", responseCode.ordinal());
        prepareSlip(activationRepository, batchRepository, mainActivity, transactionEntity, transactionCode, false);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * It prepares and prints the slip.
     */
    public void prepareSlip(ActivationRepository activationRepository, BatchRepository batchRepository, MainActivity mainActivity,
                                TransactionEntity transactionEntity, TransactionCode transactionCode, boolean isCopy) {
        SalePrintHelper salePrintHelper = new SalePrintHelper();
        String customerSlipData = salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.CARDHOLDER_SLIP, mainActivity, null, null, isCopy);
        String merchantSlipData = salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), transactionEntity, transactionCode, SlipType.MERCHANT_SLIP, mainActivity, null, null, isCopy);
        printSlip(customerSlipData, mainActivity);
        printSlip(merchantSlipData, mainActivity);
    }

    public void printSlip(String printText, MainActivity mainActivity) {
        StyledString styledText = new StyledString();
        styledText.addStyledText(printText);
        styledText.finishPrintingProcedure();
        styledText.print(PrinterService.getService(mainActivity.getApplicationContext()));
    }

    public void prepareDummyResponse(TransactionViewModel transactionViewModel, ActivationRepository activationRepository, BatchRepository batchRepository,
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

        bundle.putString("RefundInfo", getRefundInfo(null, cardNo, price, activationRepository, batchRepository));
        bundle.putString("RefNo", String.valueOf(32134323));
        bundle.putInt("PaymentType", paymentType);
        SalePrintHelper salePrintHelper = new SalePrintHelper();
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setBaPAN(cardNo);
        transactionEntity.setBaCustomerName(ownerName);
        transactionEntity.setUlAmount(price);
        if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("customerSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), null, TransactionCode.SALE, SlipType.CARDHOLDER_SLIP, mainActivity, null, null, false));
        }
        if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
            bundle.putString("merchantSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transactionEntity, activationRepository, batchRepository), null, TransactionCode.SALE, SlipType.MERCHANT_SLIP, mainActivity, null, null, false));
        }
        resultIntent.putExtras(bundle);
        transactionViewModel.setIntentLiveData(resultIntent);
    }

    /**
     * @return refundInfo which is Json with necessary components
     */
    private String getRefundInfo(TransactionEntity transactionEntity, String cardNumber, int amount,
                                 ActivationRepository activationRepository, BatchRepository batchRepository) {
        JSONObject json = new JSONObject();
        try {
            json.put("BatchNo", batchRepository.getBatchNo());
            json.put("TxnNo", batchRepository.getGroupSN());
            json.put("Amount", amount);
            json.put("MID", activationRepository.getMerchantId());
            json.put("TID", activationRepository.getTerminalId());
            json.put("CardNo", StringHelper.MaskTheCardNo(cardNumber));
            if (transactionEntity != null) {
                json.put("RefNo", transactionEntity.getRefNo());
                json.put("AuthCode", transactionEntity.getAuthCode());
                json.put("TranDate", transactionEntity.getBaTranDate());
                if (transactionEntity.getbInstCnt() > 0) {
                    json.put("InstCount", transactionEntity.getbInstCnt());
                    json.put("InstAmount", transactionEntity.getUlAmount2()/transactionEntity.getbInstCnt());
                } else {
                    json.put("InstCount", 0);
                    json.put("InstAmount", 0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
