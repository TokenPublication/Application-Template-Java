package com.example.application_template_jmvvm.ui.posTxn;

import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.data.repository.BatchRepository;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BatchViewModel extends ViewModel {
    @Inject
    public BatchRepository batchRepository;

    @Inject
    public BatchViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public BatchRepository getBatchRepository() {
        return batchRepository;
    }

    public int getBatchNo() {
        return batchRepository.getBatchNo();
    }

    public int getGroupSN() {
        return batchRepository.getGroupSN();
    }

    public String getPreviousBatchSlip() {
        return batchRepository.getPreviousBatchSlip();
    }

    public void updateBatchNo(int batchNo) {
        batchRepository.updateBatchNo(batchNo);
    }

    public void updateBatchSlip(String batchSlip, Integer batchNo) {
        batchRepository.updateBatchSlip(batchSlip, batchNo);
    }

    public void updateGUPSN(int groupSn) {
        batchRepository.updateGUPSN(groupSn);
    }

    public void deleteAll() {
        batchRepository.deleteAll();
    }

}