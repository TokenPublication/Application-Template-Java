package com.example.application_template_jmvvm.data.repository;

import android.content.Intent;
import android.os.Bundle;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.batch.BatchDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.example.application_template_jmvvm.utils.printHelpers.BatchClosePrintHelper;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.token.printerlib.PrinterService;
import com.token.printerlib.StyledString;
import com.token.uicomponents.infodialog.InfoDialog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * This class combines Batch DAO operations with other functionalities
 * for getting response and preparing batch intent data with batch slip.
 */
public class BatchRepository {
    private BatchDao batchDao;

    @Inject
    public BatchRepository(BatchDao batchDao) {
        this.batchDao = batchDao;
        initializeBatch();
    }

    private void initializeBatch() {
        if (batchDao.getAllBatch().isEmpty()) {
            BatchDB batch = new BatchDB();
            batch.setCol_ulSTN(0);
            batch.setCol_ulGUP_SN(1);
            batch.setCol_batchNo(1);
            batchDao.insertBatch(batch);
        }
    }

    public String prepareSlip(MainActivity mainActivity, ActivationRepository activationRepository, BatchRepository batchRepository,
                              List<TransactionEntity> transactionList, boolean isCopy) {
        BatchClosePrintHelper batchClosePrintHelper = new BatchClosePrintHelper();
        return batchClosePrintHelper.batchText(mainActivity, String.valueOf(batchRepository.getBatchNo()), activationRepository, transactionList, isCopy);
    }

    public BatchCloseResponse prepareResponse(MainActivity mainActivity, BatchViewModel batchViewModel, BatchResult batchResult) {
        if (batchResult == BatchResult.SUCCESS) {   //Dummy response, always success
            batchViewModel.setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getString(R.string.batch_close) + " " + mainActivity.getString(R.string.success)));
        }
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault());
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

    public int getSTN() {
        return batchDao.getSTN();
    }

    public void updateSTN() {
        batchDao.updateSTN();
    }

    public int getGroupSN() {
        return batchDao.getGUPSN();
    }

    public void updateGUPSN() {
        batchDao.updateGUPSN();
    }

    public int getBatchNo() {
        return batchDao.getBatchNo();
    }

    public void updateBatchNo() {
        batchDao.updateBatchNo();
    }

    public String getPreviousBatchSlip() {
        return batchDao.getBatchPreviousSlip();
    }

    public void updateBatchSlip(String batchSlip, Integer batchNo) {
        batchDao.updateBatchSlip(batchSlip, batchNo);
    }

    public List<BatchDB> getAllBatch() {
        return batchDao.getAllBatch();
    }

    public void deleteAll() {
        batchDao.deleteAll();
    }
}
