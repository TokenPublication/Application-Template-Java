package com.example.application_template_jmvvm.ui.posTxn;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.repository.BatchRepository;
import com.example.application_template_jmvvm.data.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.data.service.BatchCloseResponseListener;
import com.example.application_template_jmvvm.data.service.BatchCloseService;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BatchViewModel extends ViewModel {
    private MainActivity main;
    private BatchRepository batchRepository;
    private BatchDao batchDao;
    private MutableLiveData<BatchCloseResponse> batchCloseResponseLiveData = new MutableLiveData<>();

    @Inject
    public BatchViewModel() {}

    public void setter(MainActivity main) {
        this.main = main;
        batchDao = AppTempDB.getDatabase(main.getApplication()).batchDao();
        batchRepository = new BatchRepository(batchDao);
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

    public LiveData<BatchCloseResponse> getBatchCloseResponseLiveData() {
        return batchCloseResponseLiveData;
    }

    public void performBatchClose(Context context, TransactionViewModel transactionViewModel,
                                   BatchViewModel batchViewModel, BatchCloseService batchCloseService) {
        final BatchCloseResponse[] batchCloseResponse = {new BatchCloseResponse()};
        batchCloseService.doInBackground(main, context, transactionViewModel, batchViewModel,
                new BatchCloseResponseListener() {
                    @Override
                    public void onComplete(BatchCloseResponse response) {
                        batchCloseResponse[0] = response;
                        batchCloseResponseLiveData.postValue(batchCloseResponse[0]);
                    }
        });
    }

}