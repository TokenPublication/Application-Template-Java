package com.example.application_template_jmvvm.data.repository;


import com.example.application_template_jmvvm.data.database.batch.BatchDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;

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

    public void updateBatchNo(int batchNo) {
        batchDao.updateBatchNo(batchNo);
    }

    public void updateBatchSlip(String batchSlip, Integer batchNo) {
        batchDao.updateBatchSlip(batchSlip, batchNo);
    }

    public void updateGUPSN(int groupSn) {
        batchDao.updateGUPSN(groupSn);
    }

    public void incrementGUPSN() {
        batchDao.incrementGUPSN();
    }

    public void deleteAll() {
        batchDao.deleteAll();
    }
}
