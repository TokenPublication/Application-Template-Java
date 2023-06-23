package com.example.application_template_jmvvm.data.repository;


import android.content.Intent;
import android.os.Bundle;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.batch.BatchDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.domain.printHelpers.BatchClosePrintHelper;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

public class BatchRepository {
    private BatchDao batchDao;
    private int groupSN;
    private int batchNo;
    private String previousBatchSlip;
    private List<BatchDB> allBatch;

    @Inject
    public BatchRepository(BatchDao batchDao) {
        this.batchDao = batchDao;
        groupSN = batchDao.getGUPSN();
        batchNo = batchDao.getBatchNo();
        previousBatchSlip = batchDao.getBatchPreviousSlip();
        allBatch = batchDao.getAllBatch();
        initializeBatch();
    }

    private void initializeBatch() {
        // Check if the initial row exists
        if (allBatch.isEmpty()) {
            BatchDB batch = new BatchDB();
            batch.setCol_ulGUP_SN(1);
            batch.setCol_batchNo(1);
            batchDao.insertBatch(batch);
        }
    }

    public String prepareSlip(ActivationRepository activationRepository, BatchRepository batchRepository, List<TransactionEntity> transactionList) {
        BatchClosePrintHelper batchClosePrintHelper = new BatchClosePrintHelper();
        return batchClosePrintHelper.batchText(String.valueOf(batchRepository.getBatchNo()), activationRepository, transactionList, true);
    }

    public BatchCloseResponse prepareResponse(BatchViewModel batchViewModel, BatchResult batchResult, SimpleDateFormat date) {
        if (batchResult == BatchResult.SUCCESS) {
            batchViewModel.setShowDialogLiveData("Confirmed");
        }
        return new BatchCloseResponse(batchResult, date);
    }

    public Intent prepareBatchIntent(BatchCloseResponse batchCloseResponse, MainActivity mainActivity, String slip) {
        BatchResult responseCode = batchCloseResponse.getBatchResult();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        printSlip(slip, mainActivity);
        bundle.putInt("ResponseCode", responseCode.ordinal());
        intent.putExtras(bundle);
        return intent;
    }

    public void printSlip(String printText, MainActivity mainActivity) {
        StyledString styledText = new StyledString();
        styledText.addStyledText(printText);
        styledText.finishPrintingProcedure();
        styledText.print(PrinterService.getService(mainActivity.getApplicationContext()));
    }

    public int getGroupSN() {
        return batchDao.getGUPSN();
    }

    public int getBatchNo() {
        return batchDao.getBatchNo();
    }

    public String getPreviousBatchSlip() {
        return previousBatchSlip;
    }

    public List<BatchDB> getAllBatch() {
        return allBatch;
    }

    public void updateBatchNo() {
        batchDao.updateBatchNo();
    }

    public void updateBatchSlip(String batchSlip, Integer batchNo) {
        batchDao.updateBatchSlip(batchSlip, batchNo);
    }

    public void updateGUPSN() {
        batchDao.updateGUPSN();
    }

    public void deleteAll() {
        batchDao.deleteAll();
    }
}
